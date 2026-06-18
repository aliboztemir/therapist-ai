package io.therapistai.conversation.application;

import io.therapistai.conversation.domain.MessageRole;

import java.util.Objects;
import java.util.UUID;

public record AppendMessageCommand(
        UUID conversationId,
        UUID userId,
        MessageRole role,
        String content
) {

    public AppendMessageCommand {
        Objects.requireNonNull(conversationId, "conversationId must not be null");
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(role, "role must not be null");
        Objects.requireNonNull(content, "content must not be null");

        if (content.isBlank()) {
            throw new IllegalArgumentException("content must not be blank");
        }
    }
}