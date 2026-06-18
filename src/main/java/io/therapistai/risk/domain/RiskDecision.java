package io.therapistai.risk.domain;

import java.util.Collections;
import java.util.List;
import java.util.Set;


public final class RiskDecision {

    private final RiskLevel level;
    private final Set<RiskType> riskTypes;
    private final RiskAction action;
    private final boolean crisisLocked;
    private final List<RiskReason> reasons;
    private final double confidence;       // 0.0-1.0

    private RiskDecision(Builder b) {
        this.level = b.level;
        this.riskTypes = Set.copyOf(b.riskTypes);
        this.action = b.action;
        this.crisisLocked = b.crisisLocked;
        this.reasons = Collections.unmodifiableList(b.reasons);
        this.confidence = b.confidence;
    }

    // ── factory ──────────────────────────────────────────────────────────────

    public static Builder builder() {
        return new Builder();
    }


    // ── accessors ─────────────────────────────────────────────────────────────

    public RiskLevel level() {
        return level;
    }

    public RiskAction action() {
        return action;
    }

    public List<RiskReason> reasons() {
        return reasons;
    }

    public double confidence() {
        return confidence;
    }

    @Override
    public String toString() {
        return "RiskDecision{level=" + level +
                ", action=" + action +
                ", crisisLocked=" + crisisLocked +
                ", riskTypes=" + riskTypes +
                ", confidence=" + confidence + '}';
    }

    // ── builder ───────────────────────────────────────────────────────────────

    public static final class Builder {

        private RiskLevel level = RiskLevel.NONE;
        private Set<RiskType> riskTypes = Collections.emptySet();
        private RiskAction action = RiskAction.CONTINUE_NORMAL_FLOW;
        private boolean crisisLocked = false;
        private List<RiskReason> reasons = Collections.emptyList();
        private double confidence = 0.8;

        private Builder() {
        }

        public Builder level(RiskLevel v) {
            this.level = v;
            return this;
        }

        public Builder action(RiskAction v) {
            this.action = v;
            return this;
        }

        public Builder confidence(double v) {
            this.confidence = v;
            return this;
        }

        public RiskDecision build() {
            return new RiskDecision(this);
        }
    }
}

