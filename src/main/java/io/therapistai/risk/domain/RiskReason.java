package io.therapistai.risk.domain;

/**
 * Specific reason contributing to a {@link RiskDecision}.
 *
 * <p>Multiple reasons can be active simultaneously (e.g., a keyword match AND
 * a crisis signal from the analysis module). The full list is attached to
 * {@link RiskDecision#reasons()} for observability and audit.
 *
 * <p>Pure domain enum — no Spring, JPA, or infrastructure dependencies.
 */
public enum RiskReason {

    /**
     * {@code MessageAnalysis.crisisSignalDetected} was true for this message.
     */
    ANALYSIS_CRISIS_SIGNAL,

    /**
     * Keyword/phrase scan matched a suicide-intent pattern in the message.
     */
    RULE_MATCH_SUICIDE_INTENT,

    /**
     * Keyword/phrase scan matched a self-harm pattern in the message.
     */
    RULE_MATCH_SELF_HARM,

    /**
     * Keyword/phrase scan matched an active-plan indicator in the message.
     */
    RULE_MATCH_ACTIVE_PLAN,

    /**
     * Keyword/phrase scan matched a violence-risk pattern in the message.
     */
    RULE_MATCH_VIOLENCE,

    /**
     * Keyword/phrase scan matched a substance-use pattern in the message.
     */
    RULE_MATCH_SUBSTANCE_USE,

    /**
     * The session was already in crisis lock and safety has not yet been confirmed.
     */
    PREVIOUS_CRISIS_LOCK_ACTIVE,

    /**
     * The crisis lock requires explicit safety confirmation which has not been received.
     */
    SAFETY_NOT_CONFIRMED,

    /**
     * Emotional intensity was very high (≥ 8) combined with a high-risk primary emotion.
     */
    HIGH_EMOTIONAL_INTENSITY,

    /**
     * Message contains ambiguous hopelessness language that may indicate crisis risk.
     */
    AMBIGUOUS_HOPELESSNESS_LANGUAGE
}

