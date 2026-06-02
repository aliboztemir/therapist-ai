package io.therapistai.chat.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatRequest(

        @NotBlank(message = "message must not be blank")
        @Size(max = 500, message = "message must not exceed 500 characters")
        String message,

        String userId,

        String conversationId,

        String turnstileToken

) {
}