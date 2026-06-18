package io.therapistai.risk.application;

/**
 * Application-layer port for keyword/phrase-based risk scanning.
 *
 * <p>Scans a pre-normalized message string and returns a {@link ScanReport} containing
 * all matched {@link io.therapistai.risk.domain.RiskType}s, associated
 * {@link io.therapistai.risk.domain.RiskReason}s, and the maximum
 * {@link io.therapistai.risk.domain.RiskLevel} triggered by keyword matches alone.
 *
 * <p>The infrastructure layer provides the implementation (currently
 * {@code RuleBasedRiskScanner}). This interface exists so the scanner can be unit-tested
 * independently of the full {@link RiskDetectionService} pipeline.
 *
 * <p>IMPORTANT: implementations must NOT log the raw user message.
 */
public interface RiskRuleEvaluator {

    /**
     * Scan a normalized (lowercased, whitespace-trimmed) message for risk signals.
     *
     * @param normalizedMessage the already-lowercased message; must not be null
     * @return a non-null {@link ScanReport}
     */
    ScanReport scan(String normalizedMessage);
}

