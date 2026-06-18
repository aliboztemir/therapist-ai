package io.therapistai.risk.application;

import io.therapistai.analysis.domain.MessageAnalysis;
import io.therapistai.risk.domain.RiskLevel;

import java.time.Instant;
import java.util.UUID;

/**
 * All inputs needed by the {@link RiskDetectionService} to produce a {@link io.therapistai.risk.domain.RiskDecision}.
 *
 * <p>{@code previousRiskLevel} and {@code previousCrisisLocked} represent the session's
 * last known safety state. Until a persistent session module is available these default
 * to {@link RiskLevel#NONE} and {@code false} respectively; callers must populate them
 * from session state once that module exists.
 *
 * <p>{@code safetyConfirmed} is {@code true} when the user has explicitly confirmed
 * their safety during or after a crisis lock. Used to determine whether a previous
 * crisis lock can be released.
 *
 * <p>Pure application record — no Spring, JPA, or infrastructure dependencies.
 */
public record RiskDetectionInput(
        UUID userId,
        UUID conversationId,
        String currentMessage,
        MessageAnalysis messageAnalysis,
        RiskLevel previousRiskLevel,
        boolean previousCrisisLocked,
        boolean safetyConfirmed,
        Instant now
) {
}

