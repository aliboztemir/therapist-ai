package io.therapistai.conversation.api;

import java.time.Instant;

public record ConversationSummaryResponse(
        String id,
        Instant createdAt,
        Instant updatedAt
) {
}