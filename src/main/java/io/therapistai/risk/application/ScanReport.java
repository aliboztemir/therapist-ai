package io.therapistai.risk.application;

import io.therapistai.risk.domain.RiskLevel;
import io.therapistai.risk.domain.RiskReason;
import io.therapistai.risk.domain.RiskType;

import java.util.List;
import java.util.Set;

/**
 * Result of a keyword/phrase scan produced by {@link RiskRuleEvaluator}.
 *
 * <p>{@code maxLevel} is the highest {@link RiskLevel} among all matched rules — it
 * provides the floor level before analysis-signal and emotional-intensity elevations
 * are applied.
 *
 * <p>Pure application record — no Spring, JPA, or infrastructure dependencies.
 */
public record ScanReport(
        Set<RiskType> detectedTypes,
        List<RiskReason> reasons,
        RiskLevel maxLevel
) {

    /**
     * Empty report used when no keyword rules matched.
     */
    public static ScanReport empty() {
        return new ScanReport(Set.of(), List.of(), RiskLevel.NONE);
    }

    /**
     * Returns {@code true} when at least one rule matched.
     */
    public boolean hasMatches() {
        return !detectedTypes.isEmpty();
    }
}

