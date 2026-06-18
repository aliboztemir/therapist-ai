package io.therapistai.risk.domain;

/**
 * Required chat pipeline action produced by a {@link RiskDecision}.
 *
 * <p>The chat orchestrator uses this value to decide whether to continue normal
 * prompt generation, use a supportive fallback, ask for safety clarification,
 * enter a full crisis protocol, or block unsafe content.
 *
 * <p>Pure domain enum — no Spring, JPA, or infrastructure dependencies.
 */
public enum RiskAction {

    /**
     * No risk detected — proceed with normal therapy prompt generation.
     */
    CONTINUE_NORMAL_FLOW,

    /**
     * Low-risk signal — use a warm, supportive response without escalating.
     */
    USE_SUPPORTIVE_RESPONSE,

    /**
     * Ambiguous signal — gently ask the user to clarify their safety status.
     */
    ASK_SAFETY_CLARIFICATION,

    /**
     * Clear crisis signal — bypass normal prompt, deliver crisis protocol response.
     */
    ENTER_CRISIS_MODE,

    /**
     * Previous crisis lock is active and safety has not been confirmed — stay in crisis mode.
     */
    STAY_IN_CRISIS_MODE,

    /**
     * Immediate danger or active plan detected — deliver emergency guidance and resources.
     */
    ESCALATE_TO_EMERGENCY_GUIDANCE,

    /**
     * Message contains content that cannot be safely responded to — block the response.
     */
    BLOCK_UNSAFE_RESPONSE;

    /**
     * Returns {@code true} when this action requires bypassing normal LLM generation
     * and entering the crisis response path.
     */
    public boolean isCrisis() {
        return this == ENTER_CRISIS_MODE
                || this == STAY_IN_CRISIS_MODE
                || this == ESCALATE_TO_EMERGENCY_GUIDANCE;
    }
}

