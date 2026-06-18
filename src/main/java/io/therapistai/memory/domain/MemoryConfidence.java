package io.therapistai.memory.domain;

public record MemoryConfidence(double value) {

    public MemoryConfidence {
        if (value < 0.0 || value > 1.0) {
            throw new IllegalArgumentException(
                    "MemoryConfidence must be in range [0.0, 1.0], got: " + value
            );
        }
    }

    public static final MemoryConfidence NONE = new MemoryConfidence(0.0);

    public static MemoryConfidence of(double value) {
        return new MemoryConfidence(Math.max(0.0, Math.min(1.0, value)));
    }

    public boolean isAtLeast(double threshold) {
        return value >= threshold;
    }

    @Override
    public String toString() {
        return String.format("%.2f", value);
    }
}