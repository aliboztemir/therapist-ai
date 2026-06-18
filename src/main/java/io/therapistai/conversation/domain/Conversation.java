package io.therapistai.conversation.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record Conversation(
        UUID id,
        UUID userId,
        Instant createdAt,
        Instant updatedAt
) {

    public Conversation {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        Objects.requireNonNull(updatedAt, "updatedAt must not be null");
    }

    public static Conversation create(UUID userId, Instant now) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(now, "now must not be null");

        return new Conversation(
                UUID.randomUUID(),
                userId,
                now,
                now
        );
    }

    public static Conversation create(
            UUID conversationId,
            UUID userId,
            Instant now
    ) {
        Objects.requireNonNull(conversationId, "conversationId must not be null");
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(now, "now must not be null");

        return new Conversation(
                conversationId,
                userId,
                now,
                now
        );
    }

    public Conversation touch(Instant now) {
        Objects.requireNonNull(now, "now must not be null");

        return new Conversation(
                id,
                userId,
                createdAt,
                now
        );
    }
}