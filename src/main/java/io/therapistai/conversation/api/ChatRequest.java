package io.therapistai.conversation.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.text.Normalizer;

public record ChatRequest(

        @NotBlank(message = "Message is required.")
        @Size(
                max = 4000,
                message = "Message is too long. Maximum allowed length is 4000 characters."
        )
        String message,

        String conversationId,

        String turnstileToken

) {

    public ChatRequest {
        message = sanitize(normalize(message));

        if (conversationId != null) {
            conversationId = conversationId.strip();

            if (conversationId.isBlank()) {
                conversationId = null;
            }
        }

        if (turnstileToken != null) {
            turnstileToken = turnstileToken.strip();

            if (turnstileToken.isBlank()) {
                turnstileToken = null;
            }
        }
    }

    private static String normalize(String value) {
        if (value == null) {
            return null;
        }

        return Normalizer.normalize(
                value,
                Normalizer.Form.NFC
        );
    }

    private static String sanitize(String value) {
        if (value == null) {
            return null;
        }

        String sanitized = value.replaceAll(
                "<[^>]*>",
                ""
        );

        sanitized = sanitized.replaceAll(
                "[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F-\\x9F]",
                ""
        );

        return sanitized.strip();
    }
}