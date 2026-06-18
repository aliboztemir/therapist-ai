package io.therapistai.conversation.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record ChatMessage(
        UUID id,
        UUID conversationId,
        UUID userId,
        MessageRole role,
        String content,
        int messageOrder,
        Instant createdAt
) {
    public ChatMessage {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(conversationId, "conversationId must not be null");
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(role, "role must not be null");
        Objects.requireNonNull(content, "content must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");

        if (content.isBlank()) {
            throw new IllegalArgumentException("content must not be blank");
        }

        if (messageOrder < 0) {
            throw new IllegalArgumentException("messageOrder must be >= 0");
        }
    }
}