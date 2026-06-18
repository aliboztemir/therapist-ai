package io.therapistai.conversation.api;

import java.time.Instant;

public record ConversationMessageResponse(
        String id,
        String role,
        String content,
        int messageOrder,
        Instant createdAt
) {
}