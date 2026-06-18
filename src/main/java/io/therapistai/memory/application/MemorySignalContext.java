package io.therapistai.memory.application;

import java.util.List;

public record MemorySignalContext(
        String messageType,
        String userIntent,
        List<String> themes,
        String primaryEmotion,
        String secondaryEmotion,
        String sentiment,
        int emotionalIntensity,
        List<String> cognitiveSignals,
        String disclosureLevel
) {

    public MemorySignalContext {
        messageType = normalize(messageType, "UNKNOWN");
        userIntent = normalize(userIntent, "UNKNOWN");
        themes = themes != null ? List.copyOf(themes) : List.of();
        primaryEmotion = normalize(primaryEmotion, "UNKNOWN");
        secondaryEmotion = normalize(secondaryEmotion, "UNKNOWN");
        sentiment = normalize(sentiment, "UNKNOWN");
        emotionalIntensity = Math.clamp(emotionalIntensity, 0, 10);
        cognitiveSignals = cognitiveSignals != null ? List.copyOf(cognitiveSignals) : List.of();
        disclosureLevel = normalize(disclosureLevel, "MINIMAL");
    }

    public static MemorySignalContext empty() {
        return new MemorySignalContext(
                "UNKNOWN",
                "UNKNOWN",
                List.of(),
                "UNKNOWN",
                "UNKNOWN",
                "UNKNOWN",
                0,
                List.of(),
                "MINIMAL"
        );
    }

    private static String normalize(String value, String fallback) {
        return value != null && !value.isBlank()
                ? value.strip()
                : fallback;
    }
}