package io.therapistai.prompt.domain;

import java.util.Objects;

public record FinalPrompt(
        String systemPrompt,
        String userPrompt
) {
    public FinalPrompt {
        Objects.requireNonNull(systemPrompt, "systemPrompt must not be null");
        Objects.requireNonNull(userPrompt, "userPrompt must not be null");

        if (systemPrompt.isBlank()) {
            throw new IllegalArgumentException("systemPrompt must not be blank");
        }

        if (userPrompt.isBlank()) {
            throw new IllegalArgumentException("userPrompt must not be blank");
        }
    }
}