package io.therapistai.conversation.application;

import io.therapistai.conversation.domain.ChatMessage;
import io.therapistai.conversation.domain.ChatMessageRepository;
import io.therapistai.conversation.domain.Conversation;
import io.therapistai.conversation.domain.MessageRole;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Service
public class MessageService {

    private final ConversationService conversationService;
    private final ChatMessageRepository chatMessageRepository;
    private final Clock clock;

    public MessageService(
            ConversationService conversationService,
            ChatMessageRepository chatMessageRepository,
            Clock clock
    ) {
        this.conversationService = conversationService;
        this.chatMessageRepository = chatMessageRepository;
        this.clock = clock;
    }

    public ChatMessage appendUserMessage(
            UUID userId,
            UUID conversationId,
            String content
    ) {
        return appendMessage(userId, conversationId, MessageRole.USER, content);
    }

    public ChatMessage appendAssistantMessage(
            UUID userId,
            UUID conversationId,
            String content
    ) {
        return appendMessage(userId, conversationId, MessageRole.ASSISTANT, content);
    }

    private ChatMessage appendMessage(
            UUID userId,
            UUID conversationId,
            MessageRole role,
            String content
    ) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(role, "role must not be null");
        Objects.requireNonNull(content, "content must not be null");

        Conversation conversation = conversationService.loadOrCreate(
                userId,
                conversationId
        );

        UUID effectiveConversationId = conversation.id();

        int messageOrder = chatMessageRepository.nextMessageOrder(
                effectiveConversationId
        );

        Instant now = Instant.now(clock);

        ChatMessage message = new ChatMessage(
                UUID.randomUUID(),
                effectiveConversationId,
                userId,
                role,
                content,
                messageOrder,
                now
        );

        ChatMessage saved = chatMessageRepository.save(message);

        conversationService.touch(
                userId,
                effectiveConversationId
        );

        return saved;
    }
}