package io.therapistai.risk.application;

import io.therapistai.risk.domain.RiskAction;
import io.therapistai.risk.domain.RiskLevel;
import io.therapistai.risk.domain.RiskType;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Application policy that maps a detected {@link RiskLevel} and {@link RiskType}s
 * to the appropriate {@link RiskAction} the chat orchestrator must execute.
 *
 * <p>Decision table:
 * <table border="1">
 *   <tr><th>Condition</th><th>Action</th></tr>
 *   <tr><td>Previous crisis lock active</td><td>STAY_IN_CRISIS_MODE</td></tr>
 *   <tr><td>CRISIS + ACTIVE_PLAN or MEDICAL_EMERGENCY</td><td>ESCALATE_TO_EMERGENCY_GUIDANCE</td></tr>
 *   <tr><td>CRISIS (other)</td><td>ENTER_CRISIS_MODE</td></tr>
 *   <tr><td>HIGH</td><td>ASK_SAFETY_CLARIFICATION</td></tr>
 *   <tr><td>MEDIUM + SUBSTANCE_USE</td><td>USE_SUPPORTIVE_RESPONSE</td></tr>
 *   <tr><td>MEDIUM (other)</td><td>ASK_SAFETY_CLARIFICATION</td></tr>
 *   <tr><td>LOW</td><td>USE_SUPPORTIVE_RESPONSE</td></tr>
 *   <tr><td>NONE</td><td>CONTINUE_NORMAL_FLOW</td></tr>
 * </table>
 *
 * <p>No JPA or external dependencies — stateless, synchronous, deterministic.
 */
@Component
public class CrisisResponsePolicy {

    /**
     * Determines the {@link RiskAction} based on the detected level, types, and lock state.
     *
     * @param level                detected risk level
     * @param types                detected risk type categories
     * @param previousCrisisLocked whether a crisis lock was already active before this message
     * @return the required pipeline action; never null
     */
    public RiskAction determineAction(RiskLevel level, Set<RiskType> types,
                                      boolean previousCrisisLocked) {
        if (previousCrisisLocked) {
            return RiskAction.STAY_IN_CRISIS_MODE;
        }
        return switch (level) {
            case CRISIS -> {
                if (types.contains(RiskType.ACTIVE_PLAN)
                        || types.contains(RiskType.MEDICAL_EMERGENCY)) {
                    yield RiskAction.ESCALATE_TO_EMERGENCY_GUIDANCE;
                }
                yield RiskAction.ENTER_CRISIS_MODE;
            }
            case HIGH -> RiskAction.ASK_SAFETY_CLARIFICATION;
            case MEDIUM -> types.contains(RiskType.SUBSTANCE_USE)
                    ? RiskAction.USE_SUPPORTIVE_RESPONSE
                    : RiskAction.ASK_SAFETY_CLARIFICATION;
            case LOW -> RiskAction.USE_SUPPORTIVE_RESPONSE;
            case NONE -> RiskAction.CONTINUE_NORMAL_FLOW;
        };
    }
}

