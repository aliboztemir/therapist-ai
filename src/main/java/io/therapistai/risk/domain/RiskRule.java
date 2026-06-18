package io.therapistai.risk.domain;

import java.util.List;

/**
 * Immutable definition of a single risk-detection rule.
 *
 * <p>A rule consists of a set of normalized (lowercase) phrases to match against
 * the user message, the {@link RiskType} it signals, the {@link RiskReason} to
 * attach to the {@link RiskDecision}, and the minimum {@link RiskLevel} it triggers.
 *
 * <p>Used by the rule-based scanner in the infrastructure layer.
 *
 * <p>Pure domain record — no Spring, JPA, or infrastructure dependencies.
 */
public record RiskRule(
        String id,
        List<String> phrases,       // all lowercase, normalized
        RiskType riskType,
        RiskReason reason,
        RiskLevel level
) {

    /**
     * Returns {@code true} if {@code normalizedMessage} contains any of this rule's phrases.
     *
     * @param normalizedMessage the message already lowercased and whitespace-normalized
     */
    public boolean matches(String normalizedMessage) {
        for (String phrase : phrases) {
            if (normalizedMessage.contains(phrase)) {
                return true;
            }
        }
        return false;
    }
}

