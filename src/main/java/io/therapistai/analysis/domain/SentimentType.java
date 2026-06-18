package io.therapistai.analysis.domain;

/**
 * Overall sentiment direction of the user's message.
 *
 * <p>Pure domain enum — no Spring, JPA, or infrastructure dependencies.
 */
public enum SentimentType {
    POSITIVE,
    NEUTRAL,
    NEGATIVE,
    MIXED,
    UNKNOWN
}

