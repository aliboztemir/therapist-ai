package io.therapistai.conversation.api;

import java.util.Objects;

public record ChatResponse(

        String answer,

        String conversationId,

        String messageId

) {

    public ChatResponse {
        Objects.requireNonNull(answer, "answer must not be null");
        Objects.requireNonNull(conversationId, "conversationId must not be null");
        Objects.requireNonNull(messageId, "messageId must not be null");
    }
}