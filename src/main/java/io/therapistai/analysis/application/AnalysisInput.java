package io.therapistai.analysis.application;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record AnalysisInput(
        UUID userId,
        UUID conversationId,
        UUID messageId,
        String currentMessage,
        List<AnalysisHistoryMessage> recentHistory
) {

    public AnalysisInput {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(conversationId, "conversationId must not be null");
        Objects.requireNonNull(messageId, "messageId must not be null");
        Objects.requireNonNull(currentMessage, "currentMessage must not be null");

        if (currentMessage.isBlank()) {
            throw new IllegalArgumentException("currentMessage must not be blank");
        }

        recentHistory = recentHistory != null ? List.copyOf(recentHistory) : List.of();
    }

    public record AnalysisHistoryMessage(
            UUID messageId,
            String role,
            String content,
            int messageOrder,
            Instant createdAt
    ) {

        public AnalysisHistoryMessage {
            Objects.requireNonNull(messageId, "messageId must not be null");
            Objects.requireNonNull(role, "role must not be null");
            Objects.requireNonNull(content, "content must not be null");
            Objects.requireNonNull(createdAt, "createdAt must not be null");

            if (role.isBlank()) {
                throw new IllegalArgumentException("role must not be blank");
            }

            if (content.isBlank()) {
                throw new IllegalArgumentException("content must not be blank");
            }

            if (messageOrder < 0) {
                throw new IllegalArgumentException("messageOrder must be >= 0");
            }
        }
    }
}