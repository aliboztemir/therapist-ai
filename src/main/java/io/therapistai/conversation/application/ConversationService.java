package io.therapistai.conversation.application;

import io.therapistai.conversation.domain.Conversation;
import io.therapistai.conversation.domain.ConversationRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Service
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final Clock clock;

    public ConversationService(
            ConversationRepository conversationRepository,
            Clock clock
    ) {
        this.conversationRepository = conversationRepository;
        this.clock = clock;
    }

    public Conversation loadOrCreate(
            UUID userId,
            UUID requestedConversationId
    ) {
        Objects.requireNonNull(userId, "userId must not be null");

        if (requestedConversationId == null) {
            return create(userId);
        }

        Conversation conversation = conversationRepository
                .findById(requestedConversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found."));

        enforceOwnership(conversation, userId);

        return conversation;
    }

    public Conversation create(UUID userId) {
        Objects.requireNonNull(userId, "userId must not be null");

        Instant now = Instant.now(clock);

        Conversation conversation = Conversation.create(
                userId,
                now
        );

        return conversationRepository.save(conversation);
    }

    public Conversation touch(
            UUID userId,
            UUID conversationId
    ) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(conversationId, "conversationId must not be null");

        Conversation conversation = conversationRepository
                .findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found."));

        enforceOwnership(conversation, userId);

        Conversation updated = conversation.touch(
                Instant.now(clock)
        );

        return conversationRepository.save(updated);
    }

    public void validateOwnership(
            UUID userId,
            UUID conversationId
    ) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(conversationId, "conversationId must not be null");

        boolean belongsToUser = conversationRepository.belongsToUser(
                conversationId,
                userId
        );

        if (!belongsToUser) {
            throw new AccessDeniedException("Conversation does not belong to the authenticated user.");
        }
    }

    public void enforceOwnership(
            Conversation conversation,
            UUID userId
    ) {
        Objects.requireNonNull(conversation, "conversation must not be null");
        Objects.requireNonNull(userId, "userId must not be null");

        if (!conversation.userId().equals(userId)) {
            throw new AccessDeniedException("Conversation does not belong to the authenticated user.");
        }
    }
}