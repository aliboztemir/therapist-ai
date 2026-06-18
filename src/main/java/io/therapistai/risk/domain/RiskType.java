package io.therapistai.risk.domain;

/**
 * Category of safety risk detected in a user message.
 *
 * <p>Pure domain enum — no Spring, JPA, or infrastructure dependencies.
 */
public enum RiskType {
    NONE,
    SUICIDAL_IDEATION,
    SELF_HARM,
    ACTIVE_PLAN,
    VIOLENCE_RISK,
    SUBSTANCE_USE,
    ABUSE_OR_COERCION,
    PANIC_OR_ACUTE_DISTRESS,
    MEDICAL_EMERGENCY,
    UNKNOWN
}

