package io.therapistai.coreflow;

import io.therapistai.ai.application.AIProviderGateway;
import io.therapistai.ai.domain.AIRequest;
import io.therapistai.ai.domain.AIResponse;
import io.therapistai.analysis.application.AnalysisInput;
import io.therapistai.analysis.application.MessageAnalysisService;
import io.therapistai.analysis.domain.MessageAnalysis;
import io.therapistai.analysis.domain.MessageType;
import io.therapistai.auth.UserIdentity;
import io.therapistai.conversation.api.ChatRequest;
import io.therapistai.conversation.api.ChatResponse;
import io.therapistai.conversation.application.ChatMessageValidator;
import io.therapistai.conversation.application.ConversationHistoryService;
import io.therapistai.conversation.application.ConversationService;
import io.therapistai.conversation.application.MessageService;
import io.therapistai.conversation.domain.ChatMessage;
import io.therapistai.conversation.domain.Conversation;
import io.therapistai.memory.application.MemoryExtractionCommand;
import io.therapistai.memory.application.MemoryRetrievalQuery;
import io.therapistai.memory.application.MemoryService;
import io.therapistai.memory.application.MemorySignalContext;
import io.therapistai.memory.domain.MemorySnapshot;
import io.therapistai.prompt.application.PromptAssemblyService;
import io.therapistai.prompt.domain.FinalPrompt;
import io.therapistai.prompt.domain.PromptInput;
import io.therapistai.security.application.UserProfileService;
import io.therapistai.security.domain.UserProfile;
import io.therapistai.security.turnstile.TurnstileVerificationException;
import io.therapistai.security.turnstile.TurnstileVerificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class CoreflowOrchestrator {

    private static final Logger log =
            LoggerFactory.getLogger(CoreflowOrchestrator.class);

    private static final String DEFAULT_USER_NAME = "User";

    private static final String FALLBACK_ASSISTANT_RESPONSE =
            "Şu anda yanıt üretirken teknik bir sorun oluştu. Lütfen birkaç dakika sonra tekrar deneyin.";

    private final TurnstileVerificationService turnstile;
    private final UserProfileService userProfileService;
    private final ConversationService conversationService;
    private final MessageService messageService;
    private final ConversationHistoryService conversationHistoryService;
    private final ChatMessageValidator chatMessageValidator;
    private final MessageAnalysisService messageAnalysisService;
    private final MemoryService memoryService;
    private final PromptAssemblyService promptAssemblyService;
    private final AIProviderGateway aiGateway;

    public CoreflowOrchestrator(
            TurnstileVerificationService turnstile,
            UserProfileService userProfileService,
            ConversationService conversationService,
            MessageService messageService,
            ConversationHistoryService conversationHistoryService,
            ChatMessageValidator chatMessageValidator,
            MessageAnalysisService messageAnalysisService,
            MemoryService memoryService,
            PromptAssemblyService promptAssemblyService,
            AIProviderGateway aiGateway
    ) {
        this.turnstile = turnstile;
        this.userProfileService = userProfileService;
        this.conversationService = conversationService;
        this.messageService = messageService;
        this.conversationHistoryService = conversationHistoryService;
        this.chatMessageValidator = chatMessageValidator;
        this.messageAnalysisService = messageAnalysisService;
        this.memoryService = memoryService;
        this.promptAssemblyService = promptAssemblyService;
        this.aiGateway = aiGateway;
    }

    public ChatResponse chat(
            CoreflowChatCommand command
    ) {
        Objects.requireNonNull(command, "command must not be null");

        ChatTurnContext turn =
                prepareTurn(command.authentication(), command.request());

        ChatMessage userMessage =
                appendUserMessage(turn);

        MessageAnalysis analysis =
                analyzeMessageSafely(turn, userMessage);

        TurnContext ctx =
                buildInitialTurnContext(turn, userMessage, analysis);

        ctx = retrieveMemorySnapshot(ctx);

        AIResponse assistantResponse =
                generateAssistantResponseSafely(turn, ctx);

        ChatMessage assistantMessage =
                appendAssistantMessage(turn, assistantResponse);

        extractAndPersistMemorySafely(ctx);

        return new ChatResponse(
                assistantResponse.content(),
                turn.conversation().id().toString(),
                assistantMessage.id().toString()
        );
    }

    private ChatTurnContext prepareTurn(
            Authentication authentication,
            ChatRequest request
    ) {
        Objects.requireNonNull(authentication, "authentication must not be null");
        Objects.requireNonNull(request, "request must not be null");

        verifyTurnstile(request.turnstileToken());

        UUID userId =
                requireUserId(authentication);

        UUID requestedConversationId =
                parseConversationId(request.conversationId());

        String currentMessage =
                normalizeMessage(request.message());

        chatMessageValidator.validate(currentMessage);

        UserProfile userProfile =
                loadUserProfile(userId);

        Conversation conversation =
                conversationService.loadOrCreate(
                        userId,
                        requestedConversationId
                );

        List<ChatMessage> history =
                conversationHistoryService.getRecentMessages(
                        userId,
                        conversation.id()
                );

        return new ChatTurnContext(
                userId,
                requestedConversationId,
                conversation,
                requestedConversationId == null,
                userProfile,
                currentMessage,
                history
        );
    }

    private UserProfile loadUserProfile(UUID userId) {
        Objects.requireNonNull(userId, "userId must not be null");

        UserProfile profile =
                userProfileService.loadProfile(userId);

        if (!profile.onboardingCompleted()) {
            throw new AccessDeniedException("ONBOARDING_REQUIRED");
        }

        return profile;
    }

    private ChatMessage appendUserMessage(
            ChatTurnContext turn
    ) {
        return messageService.appendUserMessage(
                turn.userId(),
                turn.conversation().id(),
                turn.currentMessage()
        );
    }

    private MessageAnalysis analyzeMessageSafely(
            ChatTurnContext turn,
            ChatMessage userMessage
    ) {
        try {
            return messageAnalysisService.analyze(
                    new AnalysisInput(
                            turn.userId(),
                            turn.conversation().id(),
                            userMessage.id(),
                            turn.currentMessage(),
                            toAnalysisHistory(turn.historyBeforeCurrentMessage())
                    )
            );

        } catch (Exception ex) {
            log.warn(
                    "coreflow.analysis.failed userId={} conversationId={} messageId={}",
                    turn.userId(),
                    turn.conversation().id(),
                    userMessage.id(),
                    ex
            );

            return MessageAnalysis.safeDefault("analysis-failed");
        }
    }

    private TurnContext buildInitialTurnContext(
            ChatTurnContext turn,
            ChatMessage userMessage,
            MessageAnalysis analysis
    ) {
        return new TurnContext(
                userMessage.id(),
                turn.userId(),
                turn.conversation().id(),
                turn.isNewConversation(),
                turn.userProfile(),
                turn.currentMessage(),
                toRecentMessageContents(turn.historyBeforeCurrentMessage()),
                analysis,
                MemorySnapshot.empty()
        );
    }

    private TurnContext retrieveMemorySnapshot(
            TurnContext ctx
    ) {
        Objects.requireNonNull(ctx, "ctx must not be null");

        try {
            if (isGreeting(ctx.analysis())) {
                return ctx.withMemorySnapshot(MemorySnapshot.empty());
            }

            MemorySnapshot snapshot =
                    memoryService.retrieve(
                            new MemoryRetrievalQuery(
                                    ctx.userId(),
                                    ctx.conversationId(),
                                    ctx.currentMessage(),
                                    toMemorySignalContext(ctx.analysis()),
                                    MemoryRetrievalQuery.DEFAULT_LIMIT
                            )
                    );

            log.debug(
                    "coreflow.memory.retrieved userId={} conversationId={} messageId={} snapshotSize={}",
                    ctx.userId(),
                    ctx.conversationId(),
                    ctx.messageId(),
                    snapshot != null ? snapshot.size() : 0
            );

            return ctx.withMemorySnapshot(
                    snapshot != null
                            ? snapshot
                            : MemorySnapshot.empty()
            );

        } catch (Exception ex) {
            log.warn(
                    "coreflow.memory.retrieval.failed userId={} conversationId={} messageId={} error={}",
                    ctx.userId(),
                    ctx.conversationId(),
                    ctx.messageId(),
                    ex.getMessage()
            );

            return ctx.withMemorySnapshot(MemorySnapshot.empty());
        }
    }

    private AIResponse generateAssistantResponseSafely(
            ChatTurnContext turn,
            TurnContext ctx
    ) {
        try {
            FinalPrompt prompt =
                    promptAssemblyService.assemble(
                            new PromptInput(
                                    null,
                                    resolveDisplayName(turn.userProfile()),
                                    ctx.memorySnapshot(),
                                    turn.currentMessage()
                            )
                    );

            return aiGateway.generate(
                    new AIRequest(
                            toSinglePrompt(prompt)
                    )
            );

        } catch (Exception ex) {
            log.error(
                    "coreflow.assistant_generation.failed userId={} conversationId={} messageId={}",
                    turn.userId(),
                    turn.conversation().id(),
                    ctx.messageId(),
                    ex
            );

            return AIResponse.fallback(FALLBACK_ASSISTANT_RESPONSE);
        }
    }

    private ChatMessage appendAssistantMessage(
            ChatTurnContext turn,
            AIResponse response
    ) {
        return messageService.appendAssistantMessage(
                turn.userId(),
                turn.conversation().id(),
                response.content()
        );
    }

    private void extractAndPersistMemorySafely(
            TurnContext ctx
    ) {
        try {
            if (isGreeting(ctx.analysis())) {
                return;
            }

            memoryService.extractAndPersist(
                    new MemoryExtractionCommand(
                            ctx.userId(),
                            ctx.conversationId(),
                            ctx.messageId(),
                            ctx.currentMessage(),
                            toMemorySignalContext(ctx.analysis())
                    )
            );

        } catch (Exception ex) {
            log.warn(
                    "coreflow.memory_persistence.failed userId={} conversationId={} messageId={}",
                    ctx.userId(),
                    ctx.conversationId(),
                    ctx.messageId(),
                    ex
            );
        }
    }

    private void verifyTurnstile(
            String token
    ) {
        if (!turnstile.verify(token)) {
            throw new TurnstileVerificationException(
                    "Turnstile verification failed."
            );
        }
    }

    private static UUID requireUserId(
            Authentication authentication
    ) {
        Object principal =
                authentication.getPrincipal();

        if (principal instanceof UserIdentity identity) {
            return identity.userId();
        }

        throw new IllegalStateException(
                "Authenticated principal does not expose userId."
        );
    }

    private static UUID parseConversationId(
            String conversationId
    ) {
        if (conversationId == null || conversationId.isBlank()) {
            return null;
        }

        return UUID.fromString(conversationId);
    }

    private static String normalizeMessage(
            String message
    ) {
        return message != null
                ? message.strip()
                : "";
    }

    private static boolean isGreeting(
            MessageAnalysis analysis
    ) {
        return analysis != null
                && analysis.messageType() == MessageType.GREETING;
    }

    private static MemorySignalContext toMemorySignalContext(
            MessageAnalysis analysis
    ) {
        if (analysis == null) {
            return MemorySignalContext.empty();
        }

        return new MemorySignalContext(
                analysis.messageType().name(),
                analysis.userIntent().name(),
                analysis.themes()
                        .stream()
                        .map(Enum::name)
                        .toList(),
                analysis.primaryEmotion().name(),
                analysis.secondaryEmotion().name(),
                analysis.sentiment().name(),
                analysis.emotionalIntensity(),
                analysis.cognitiveSignals()
                        .stream()
                        .map(Enum::name)
                        .toList(),
                analysis.disclosureLevel().name()
        );
    }

    private static String resolveDisplayName(
            UserProfile userProfile
    ) {
        if (userProfile == null) {
            return DEFAULT_USER_NAME;
        }

        if (userProfile.preferredName() != null
                && !userProfile.preferredName().isBlank()) {
            return userProfile.preferredName();
        }

        if (userProfile.fullName() != null
                && !userProfile.fullName().isBlank()) {
            return userProfile.fullName();
        }

        if (userProfile.username() != null
                && !userProfile.username().isBlank()) {
            return userProfile.username();
        }

        return DEFAULT_USER_NAME;
    }

    private static List<AnalysisInput.AnalysisHistoryMessage> toAnalysisHistory(
            List<ChatMessage> messages
    ) {
        if (messages == null || messages.isEmpty()) {
            return List.of();
        }

        return messages.stream()
                .map(message ->
                        new AnalysisInput.AnalysisHistoryMessage(
                                message.id(),
                                message.role().name(),
                                message.content(),
                                message.messageOrder(),
                                message.createdAt()
                        )
                )
                .toList();
    }

    private static List<String> toRecentMessageContents(
            List<ChatMessage> messages
    ) {
        if (messages == null || messages.isEmpty()) {
            return List.of();
        }

        return messages.stream()
                .map(ChatMessage::content)
                .toList();
    }

    private static String toSinglePrompt(
            FinalPrompt prompt
    ) {
        return """
                %s
                
                %s
                """.formatted(
                prompt.systemPrompt(),
                prompt.userPrompt()
        ).strip();
    }

    private record ChatTurnContext(
            UUID userId,
            UUID requestedConversationId,
            Conversation conversation,
            boolean isNewConversation,
            UserProfile userProfile,
            String currentMessage,
            List<ChatMessage> historyBeforeCurrentMessage
    ) {
    }
}