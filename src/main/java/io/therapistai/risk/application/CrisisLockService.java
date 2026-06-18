package io.therapistai.risk.application;

import io.therapistai.risk.domain.CrisisLock;
import io.therapistai.risk.domain.RiskLevel;
import io.therapistai.risk.domain.RiskReason;
import io.therapistai.risk.domain.RiskType;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Set;

/**
 * Application service that determines whether a crisis lock should be established,
 * maintained, or lifted, and constructs the corresponding {@link CrisisLock} value.
 *
 * <p>Lock rules:
 * <ul>
 *   <li>CRISIS level always locks.</li>
 *   <li>HIGH level locks when the risk types include an immediate-danger category
 *       (suicidal ideation, self-harm, active plan, violence, medical emergency).</li>
 *   <li>Any other level does not lock.</li>
 * </ul>
 *
 * <p>No JPA or external dependencies — stateless, synchronous, deterministic.
 */
@Component
public class CrisisLockService {

    /**
     * Returns {@code true} when a crisis lock should be established or maintained.
     */
    public boolean shouldLock(RiskLevel level, Set<RiskType> types) {
        if (level == RiskLevel.CRISIS) {
            return true;
        }
        if (level == RiskLevel.HIGH) {
            return types.contains(RiskType.SUICIDAL_IDEATION)
                    || types.contains(RiskType.SELF_HARM)
                    || types.contains(RiskType.ACTIVE_PLAN)
                    || types.contains(RiskType.VIOLENCE_RISK)
                    || types.contains(RiskType.MEDICAL_EMERGENCY);
        }
        return false;
    }

    /**
     * Builds a {@link CrisisLock} based on whether locking is required.
     */
    public CrisisLock buildLock(boolean lock, List<RiskReason> reasons, Instant now) {
        if (!lock) {
            return CrisisLock.unlocked();
        }
        String reason = reasons.isEmpty() ? "Risk threshold exceeded" : reasons.getFirst().name();
        return CrisisLock.lockedNow(reason, now);
    }
}

