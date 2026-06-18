package io.therapistai.analysis.domain;

/**
 * The level of personal self-disclosure present in the user's latest message.
 *
 * <p>Pure domain enum — no Spring, JPA, or infrastructure dependencies.
 */
public enum DisclosureLevel {

    MINIMAL,

    SURFACE,

    PERSONAL,

    DEEPLY_PERSONAL
}