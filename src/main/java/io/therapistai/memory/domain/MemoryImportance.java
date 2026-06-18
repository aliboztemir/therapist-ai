package io.therapistai.memory.domain;

public enum MemoryImportance {

    TRIVIAL(1),
    LOW(3),
    MEDIUM(5),
    HIGH(7),
    CRITICAL(10);

    private final int score;

    MemoryImportance(int score) {
        this.score = score;
    }

    public int score() {
        return score;
    }

    public static MemoryImportance fromScore(int score) {
        if (score >= 9) {
            return CRITICAL;
        }

        if (score >= 7) {
            return HIGH;
        }

        if (score >= 4) {
            return MEDIUM;
        }

        if (score >= 2) {
            return LOW;
        }

        return TRIVIAL;
    }
}