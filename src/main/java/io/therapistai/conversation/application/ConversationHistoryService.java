package io.therapistai.conversation.application;

import io.therapistai.conversation.domain.ChatMessage;
import io.therapistai.conversation.domain.ChatMessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class ConversationHistoryService {

    private static final int DEFAULT_RECENT_HISTORY_LIMIT = 6;

    private final ConversationService conversationService;
    private final ChatMessageRepository chatMessageRepository;

    public ConversationHistoryService(
            ConversationService conversationService,
            ChatMessageRepository chatMessageRepository
    ) {
        this.conversationService = conversationService;
        this.chatMessageRepository = chatMessageRepository;
    }

    public List<ChatMessage> getRecentMessages(
            UUID userId,
            UUID conversationId
    ) {
        return getRecentMessages(
                userId,
                conversationId,
                DEFAULT_RECENT_HISTORY_LIMIT
        );
    }

    public List<ChatMessage> getRecentMessages(
            UUID userId,
            UUID conversationId,
            int limit
    ) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(conversationId, "conversationId must not be null");

        conversationService.validateOwnership(
                userId,
                conversationId
        );

        return chatMessageRepository.findRecentMessages(
                conversationId,
                limit
        );
    }

    public List<ChatMessage> getConversationHistory(
            UUID userId,
            UUID conversationId
    ) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(conversationId, "conversationId must not be null");

        conversationService.validateOwnership(
                userId,
                conversationId
        );

        return chatMessageRepository.findConversationHistory(
                conversationId
        );
    }
}