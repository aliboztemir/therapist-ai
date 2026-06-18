package io.therapistai.ai.domain;

import java.util.Objects;

public record AIRequest(
        String prompt
) {

    public AIRequest {
        Objects.requireNonNull(prompt, "prompt must not be null");

        if (prompt.isBlank()) {
            throw new IllegalArgumentException("prompt must not be blank");
        }
    }
}