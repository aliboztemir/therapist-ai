package io.therapistai.analysis.application;

import java.util.Objects;

public record AnalysisPrompt(
        String content
) {

    public AnalysisPrompt {
        Objects.requireNonNull(content, "content must not be null");

        if (content.isBlank()) {
            throw new IllegalArgumentException("content must not be blank");
        }
    }
}