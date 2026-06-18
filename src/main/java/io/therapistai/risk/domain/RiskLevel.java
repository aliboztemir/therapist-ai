package io.therapistai.risk.domain;

/**
 * Ordered severity levels for detected risk.
 *
 * <p>{@code ordinal()} reflects severity — higher is more severe.
 * This ordering is used in comparison logic (e.g., {@code level.ordinal() < CRISIS.ordinal()}).
 *
 * <p>Pure domain enum — no Spring, JPA, or infrastructure dependencies.
 */
public enum RiskLevel {
    NONE,
    LOW,
    MEDIUM,
    HIGH,
    CRISIS
}

