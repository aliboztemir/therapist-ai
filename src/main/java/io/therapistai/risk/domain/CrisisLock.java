package io.therapistai.risk.domain;

import java.time.Instant;

/**
 * Immutable value object representing an active or inactive crisis lock on a session.
 *
 * <p>When {@code locked} is {@code true} the session is in a safety-restricted state.
 * Subsequent messages must not resume normal therapy flow until the lock is released
 * (typically by safety confirmation from the user or manual intervention).
 *
 * <p>{@code lockedAt} and {@code expiresAt} are optional metadata — they may be null
 * in implementations that do not yet support time-based lock expiry.
 *
 * <p>Pure domain record — no Spring, JPA, or infrastructure dependencies.
 */
public record CrisisLock(
        boolean locked,
        String reason,      // nullable — why the lock was set
        Instant lockedAt,    // nullable
        Instant expiresAt    // nullable — null means no automatic expiry
) {

    /**
     * Convenience factory for an unlocked state.
     */
    public static CrisisLock unlocked() {
        return new CrisisLock(false, null, null, null);
    }

    /**
     * Convenience factory for a locked state with a reason and timestamp.
     */
    public static CrisisLock lockedNow(String reason, Instant now) {
        return new CrisisLock(true, reason, now, null);
    }
}

