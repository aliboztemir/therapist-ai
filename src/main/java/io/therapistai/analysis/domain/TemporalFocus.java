package io.therapistai.analysis.domain;

/**
 * The primary time orientation of the user's latest message.
 *
 * <p>Pure domain enum — no Spring, JPA, or infrastructure dependencies.
 */
public enum TemporalFocus {

    PAST,

    PRESENT,

    FUTURE,

    MIXED,

    UNKNOWN
}