package io.therapistai.ai.domain;

import java.time.Instant;
import java.util.Objects;

public record AIResponse(
        String content,
        String model,
        Instant createdAt
) {

    public AIResponse {
        Objects.requireNonNull(content, "content must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");

        if (content.isBlank()) {
            throw new IllegalArgumentException("content must not be blank");
        }
    }

    public static AIResponse fallback(String content) {
        return new AIResponse(
                content,
                "fallback",
                Instant.now()
        );
    }
}