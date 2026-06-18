package io.therapistai.conversation.api;

import java.util.List;

public record ConversationMessagesResponse(
        String conversationId,
        List<ConversationMessageResponse> messages
) {
}