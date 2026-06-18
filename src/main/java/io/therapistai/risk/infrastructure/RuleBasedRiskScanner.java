package io.therapistai.risk.infrastructure;

import io.therapistai.analysis.domain.EmotionType;
import io.therapistai.risk.application.CrisisLockService;
import io.therapistai.risk.application.CrisisResponsePolicy;
import io.therapistai.risk.application.RiskDetectionService;
import io.therapistai.risk.application.RiskRuleEvaluator;
import io.therapistai.risk.application.ScanReport;
import io.therapistai.risk.domain.RiskLevel;
import io.therapistai.risk.domain.RiskReason;
import io.therapistai.risk.domain.RiskRule;
import io.therapistai.risk.domain.RiskType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
public class RuleBasedRiskScanner implements RiskDetectionService, RiskRuleEvaluator {

    private static final Logger log = LoggerFactory.getLogger(RuleBasedRiskScanner.class);

    // Minimum emotional intensity score to trigger a HIGH_EMOTIONAL_INTENSITY elevation
    private static final int INTENSITY_THRESHOLD = 8;

    private static final Set<EmotionType> HIGH_RISK_EMOTIONS = EnumSet.of(
            EmotionType.HOPELESSNESS,
            EmotionType.FEAR,
            EmotionType.ANXIETY,
            EmotionType.NUMBNESS
    );

    // ── rule catalogue ────────────────────────────────────────────────────────

    private static final List<RiskRule> RULES = List.of(

            // ── ACTIVE PLAN — CRISIS ─────────────────────────────────────────────
            new RiskRule("active-plan",
                    List.of(
                            // Turkish
                            "planım var", "şimdi yapacağım", "bu gece yapacağım", "bugün yapacağım",
                            "ilaç aldım", "bıçak aldım", "az önce yaptım", "hayatıma son verdim",
                            // English
                            "i have a plan", "i will do it tonight", "i am going to do it now",
                            "i will do it today", "i took pills", "i have a knife", "i already did it"
                    ),
                    RiskType.ACTIVE_PLAN, RiskReason.RULE_MATCH_ACTIVE_PLAN, RiskLevel.CRISIS),

            // ── SUICIDAL IDEATION — CRISIS ────────────────────────────────────────
            new RiskRule("suicide-ideation",
                    List.of(
                            // Turkish
                            "intihar", "kendimi öldür", "ölmek istiyorum", "yaşamak istemiyorum",
                            "hayatıma son",
                            // English
                            "suicide", "kill myself", "want to die", "end my life",
                            "take my life", "no reason to live"
                    ),
                    RiskType.SUICIDAL_IDEATION, RiskReason.RULE_MATCH_SUICIDE_INTENT, RiskLevel.CRISIS),

            // ── SELF-HARM — CRISIS ────────────────────────────────────────────────
            new RiskRule("self-harm",
                    List.of(
                            // Turkish
                            "kendime zarar", "kendimi kes", "kendimi yaralıyorum", "bıçakla kendim",
                            // English
                            "hurt myself", "self harm", "self-harm", "cut myself", "cutting myself",
                            "burn myself", "burning myself", "harm myself", "harming myself"
                    ),
                    RiskType.SELF_HARM, RiskReason.RULE_MATCH_SELF_HARM, RiskLevel.CRISIS),

            // ── OVERDOSE — CRISIS (self-harm category) ────────────────────────────
            new RiskRule("overdose",
                    List.of(
                            // Turkish
                            "çok fazla ilaç", "aşırı doz",
                            // English
                            "overdose", "took too many pills", "too many pills"
                    ),
                    RiskType.SELF_HARM, RiskReason.RULE_MATCH_SELF_HARM, RiskLevel.CRISIS),

            // ── MEDICAL EMERGENCY — CRISIS ────────────────────────────────────────
            new RiskRule("medical-emergency",
                    List.of(
                            // Turkish
                            "göğüs ağrısı", "nefes alamıyorum", "bayılacağım", "bayıldım",
                            "kalp krizi",
                            // English
                            "chest pain", "can't breathe", "cannot breathe",
                            "loss of consciousness", "unconscious", "heart attack"
                    ),
                    RiskType.MEDICAL_EMERGENCY, RiskReason.RULE_MATCH_ACTIVE_PLAN, RiskLevel.CRISIS),

            // ── VIOLENCE RISK — HIGH ──────────────────────────────────────────────
            new RiskRule("violence",
                    List.of(
                            // Turkish
                            "onu öldüreceğim", "ona zarar vereceğim", "öldürmek istiyorum",
                            "öldüreceğim",
                            // English
                            "i will kill", "going to kill", "want to kill",
                            "going to hurt", "i will hurt"
                    ),
                    RiskType.VIOLENCE_RISK, RiskReason.RULE_MATCH_VIOLENCE, RiskLevel.HIGH),

            // ── SUBSTANCE USE — MEDIUM ────────────────────────────────────────────
            new RiskRule("substance-use",
                    List.of(
                            // Turkish
                            "uyuşturucu aldım", "madde kullandım", "nüks ettim",
                            "çok içtim ve", "aşırı içki",
                            // English
                            "relapse", "using drugs", "substance use", "drug use",
                            "i drank too much", "got drunk and"
                    ),
                    RiskType.SUBSTANCE_USE, RiskReason.RULE_MATCH_SUBSTANCE_USE, RiskLevel.MEDIUM),

            // ── AMBIGUOUS HOPELESSNESS — HIGH ─────────────────────────────────────
            new RiskRule("hopelessness",
                    List.of(
                            // Turkish
                            "dayanamıyorum", "her şey mahvoldu", "çıkış yolu yok",
                            // English
                            "no way out", "can't go on", "can't take it anymore",
                            "no hope left", "nothing left to live"
                    ),
                    RiskType.SUICIDAL_IDEATION, RiskReason.AMBIGUOUS_HOPELESSNESS_LANGUAGE, RiskLevel.HIGH)
    );

    // ── constructor ───────────────────────────────────────────────────────────

    private final CrisisLockService crisisLockService;
    private final CrisisResponsePolicy crisisResponsePolicy;

    public RuleBasedRiskScanner(CrisisLockService crisisLockService,
                                CrisisResponsePolicy crisisResponsePolicy
    ) {
        this.crisisLockService = crisisLockService;
        this.crisisResponsePolicy = crisisResponsePolicy;
    }

    // ── RiskRuleEvaluator ─────────────────────────────────────────────────────

    /**
     * Scans a pre-normalized message for rule matches.
     *
     * <p>SAFETY: raw message content is NOT logged here.
     */
    @Override
    public ScanReport scan(String normalizedMessage) {
        Set<RiskType> types = new HashSet<>();
        List<RiskReason> reasons = new ArrayList<>();
        RiskLevel maxLevel = RiskLevel.NONE;

        for (RiskRule rule : RULES) {
            if (rule.matches(normalizedMessage)) {
                types.add(rule.riskType());
                addUnique(reasons, rule.reason());
                if (rule.level().ordinal() > maxLevel.ordinal()) {
                    maxLevel = rule.level();
                }
                // Continue — multiple rules may match simultaneously
            }
        }

        return new ScanReport(types, reasons, maxLevel);
    }

    private static <T> void addUnique(List<T> list, T item) {
        if (!list.contains(item)) list.add(item);
    }
}

