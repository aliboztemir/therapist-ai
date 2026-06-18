package io.therapistai.memory.domain;

import java.util.Map;

public final class MemoryCandidate {

    private final MemoryType type;
    private final MemoryKey key;
    private final String value;
    private final double confidence;
    private final Map<String, Object> metadata;

    private MemoryCandidate(
            MemoryType type,
            MemoryKey key,
            String value,
            double confidence,
            Map<String, Object> metadata
    ) {
        this.type = type;
        this.key = key;
        this.value = value;
        this.confidence = Math.clamp(confidence, 0.0, 1.0);
        this.metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static MemoryCandidate of(
            MemoryType type,
            MemoryKey key,
            String value,
            double confidence
    ) {
        return of(type, key, value, confidence, Map.of());
    }

    public static MemoryCandidate of(
            MemoryType type,
            MemoryKey key,
            String value,
            double confidence,
            Map<String, Object> metadata
    ) {
        return new MemoryCandidate(type, key, value, confidence, metadata);
    }

    public boolean isValid() {
        return type != null
                && key != null
                && type.allows(key)
                && value != null
                && !value.isBlank();
    }

    public MemoryType type() {
        return type;
    }

    public MemoryKey key() {
        return key;
    }

    public String value() {
        return value;
    }

    public double confidence() {
        return confidence;
    }

    public Map<String, Object> metadata() {
        return metadata;
    }

    public String evidenceText() {
        Object value = metadata.get("evidenceText");
        return value instanceof String text ? text : null;
    }

    @Override
    public String toString() {
        return "MemoryCandidate{" +
                "type=" + type +
                ", key=" + key +
                ", value='" + value + '\'' +
                ", confidence=" + confidence +
                ", metadata=" + metadata +
                '}';
    }
}