package io.therapistai.memory.application;

import io.therapistai.memory.domain.MemoryItem;
import io.therapistai.memory.domain.MemoryStatus;
import io.therapistai.memory.domain.MemoryType;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Component
public class MemoryRankingService {

    private static final double WEIGHT_RECENCY = 0.20;
    private static final double WEIGHT_IMPORTANCE = 0.35;
    private static final double WEIGHT_CONFIDENCE = 0.20;
    private static final double WEIGHT_RELEVANCE = 0.25;

    private static final int MIN_TOKEN_LENGTH = 4;

    public List<MemoryItem> rank(
            List<MemoryItem> items,
            String currentMessage,
            MemorySignalContext signalContext,
            Instant now
    ) {
        if (items == null || items.isEmpty()) {
            return List.of();
        }

        String normalizedMessage = normalize(currentMessage);
        MemorySignalContext effectiveSignalContext =
                signalContext != null ? signalContext : MemorySignalContext.empty();
        Instant effectiveNow = now != null ? now : Instant.now();

        return items.stream()
                .filter(item -> item != null && item.status() == MemoryStatus.ACTIVE)
                .sorted(
                        Comparator.comparingDouble(
                                (MemoryItem item) -> computeScore(
                                        item,
                                        normalizedMessage,
                                        effectiveSignalContext,
                                        effectiveNow
                                )
                        ).reversed()
                )
                .toList();
    }

    double computeScore(
            MemoryItem item,
            String normalizedMessage,
            MemorySignalContext signalContext,
            Instant now
    ) {
        double recencyScore = computeRecencyScore(item.createdAt(), now);
        double importanceScore = normalizeImportance(item.importance());
        double confidenceScore = clamp(item.confidence());
        double relevanceScore = computeRelevanceScore(
                item,
                normalizedMessage,
                signalContext
        );

        return (recencyScore * WEIGHT_RECENCY)
                + (importanceScore * WEIGHT_IMPORTANCE)
                + (confidenceScore * WEIGHT_CONFIDENCE)
                + (relevanceScore * WEIGHT_RELEVANCE);
    }

    private static double computeRecencyScore(
            Instant createdAt,
            Instant now
    ) {
        if (createdAt == null || now == null) {
            return 0.0;
        }

        long daysSince = ChronoUnit.DAYS.between(createdAt, now);

        return Math.exp(-0.05 * Math.max(0, daysSince));
    }

    private static double normalizeImportance(int importance) {
        return clamp(importance / 10.0);
    }

    private static double computeRelevanceScore(
            MemoryItem item,
            String normalizedMessage,
            MemorySignalContext signalContext
    ) {
        double score = 0.0;

        if (matchesTypeName(item, normalizedMessage)) {
            score += 0.15;
        }

        if (matchesKeyName(item, normalizedMessage)) {
            score += 0.20;
        }

        if (matchesValueToken(item, normalizedMessage)) {
            score += 0.30;
        }

        if (isThemeAligned(item.type(), signalContext)) {
            score += 0.35;
        }

        return clamp(score);
    }

    private static boolean matchesTypeName(
            MemoryItem item,
            String normalizedMessage
    ) {
        if (item.type() == null || normalizedMessage.isBlank()) {
            return false;
        }

        String normalizedType = normalize(item.type().name());

        return !normalizedType.isBlank()
                && normalizedMessage.contains(normalizedType);
    }

    private static boolean matchesKeyName(
            MemoryItem item,
            String normalizedMessage
    ) {
        if (item.key() == null || normalizedMessage.isBlank()) {
            return false;
        }

        String normalizedKey = normalize(item.key().name());

        return !normalizedKey.isBlank()
                && normalizedMessage.contains(normalizedKey);
    }

    private static boolean matchesValueToken(
            MemoryItem item,
            String normalizedMessage
    ) {
        if (item.value() == null || item.value().isBlank() || normalizedMessage.isBlank()) {
            return false;
        }

        String[] tokens = normalize(item.value()).split("\\s+");

        for (String token : tokens) {
            if (token.length() >= MIN_TOKEN_LENGTH
                    && normalizedMessage.contains(token)) {
                return true;
            }
        }

        return false;
    }

    private static boolean isThemeAligned(
            MemoryType type,
            MemorySignalContext signalContext
    ) {
        if (type == null
                || signalContext == null
                || signalContext.themes() == null
                || signalContext.themes().isEmpty()) {
            return false;
        }

        for (String theme : signalContext.themes()) {
            if (themeAligned(type, theme)) {
                return true;
            }
        }

        return false;
    }

    private static boolean themeAligned(
            MemoryType type,
            String theme
    ) {
        if (type == null || theme == null || theme.isBlank()) {
            return false;
        }

        return switch (theme.strip().toUpperCase(Locale.ROOT)) {
            case "WORK" -> isAnyOf(
                    type,
                    MemoryType.WORK_STATUS,
                    MemoryType.STRESSOR,
                    MemoryType.FEAR,
                    MemoryType.TRIGGER,
                    MemoryType.THERAPY_TOPIC,
                    MemoryType.THERAPY_GOAL
            );

            case "SCHOOL" -> isAnyOf(
                    type,
                    MemoryType.SCHOOL_STATUS,
                    MemoryType.STRESSOR,
                    MemoryType.FEAR,
                    MemoryType.TRIGGER,
                    MemoryType.THERAPY_TOPIC,
                    MemoryType.THERAPY_GOAL
            );

            case "FINANCIAL" -> isAnyOf(
                    type,
                    MemoryType.FINANCIAL_STATUS,
                    MemoryType.STRESSOR,
                    MemoryType.FEAR,
                    MemoryType.TRIGGER,
                    MemoryType.THERAPY_TOPIC,
                    MemoryType.THERAPY_GOAL
            );

            case "FAMILY" -> isAnyOf(
                    type,
                    MemoryType.PERSON,
                    MemoryType.RELATIONSHIP_STATUS,
                    MemoryType.SUPPORT_SYSTEM,
                    MemoryType.LIVING_SITUATION,
                    MemoryType.CHILDHOOD_BACKGROUND,
                    MemoryType.LIFE_EVENT,
                    MemoryType.STRESSOR,
                    MemoryType.TRIGGER
            );

            case "RELATIONSHIP" -> isAnyOf(
                    type,
                    MemoryType.RELATIONSHIP_STATUS,
                    MemoryType.SUPPORT_SYSTEM,
                    MemoryType.STRESSOR,
                    MemoryType.TRIGGER,
                    MemoryType.FEAR,
                    MemoryType.THERAPY_TOPIC,
                    MemoryType.THERAPY_GOAL
            );

            case "HEALTH" -> isAnyOf(
                    type,
                    MemoryType.SYMPTOM,
                    MemoryType.ADAPTIVE_COPING,
                    MemoryType.MALADAPTIVE_COPING,
                    MemoryType.STRESSOR,
                    MemoryType.TRIGGER,
                    MemoryType.FEAR,
                    MemoryType.THERAPY_TOPIC,
                    MemoryType.THERAPY_GOAL
            );

            case "SELF_WORTH" -> isAnyOf(
                    type,
                    MemoryType.CORE_BELIEF,
                    MemoryType.IDENTITY,
                    MemoryType.SYMPTOM,
                    MemoryType.FEAR,
                    MemoryType.TRIGGER,
                    MemoryType.THERAPY_TOPIC,
                    MemoryType.THERAPY_GOAL
            );

            case "IDENTITY" -> isAnyOf(
                    type,
                    MemoryType.IDENTITY,
                    MemoryType.CORE_BELIEF,
                    MemoryType.LIFE_EVENT,
                    MemoryType.CHILDHOOD_BACKGROUND,
                    MemoryType.THERAPY_TOPIC,
                    MemoryType.THERAPY_GOAL
            );

            case "LONELINESS" -> isAnyOf(
                    type,
                    MemoryType.RELATIONSHIP_STATUS,
                    MemoryType.SUPPORT_SYSTEM,
                    MemoryType.LIVING_SITUATION,
                    MemoryType.SYMPTOM,
                    MemoryType.CORE_BELIEF,
                    MemoryType.IDENTITY,
                    MemoryType.THERAPY_TOPIC
            );

            case "TRAUMA" -> isAnyOf(
                    type,
                    MemoryType.LIFE_EVENT,
                    MemoryType.CHILDHOOD_BACKGROUND,
                    MemoryType.CORE_BELIEF,
                    MemoryType.IDENTITY,
                    MemoryType.SYMPTOM,
                    MemoryType.TRIGGER,
                    MemoryType.FEAR,
                    MemoryType.MALADAPTIVE_COPING,
                    MemoryType.THERAPY_TOPIC,
                    MemoryType.THERAPY_GOAL
            );

            case "ADDICTION" -> isAnyOf(
                    type,
                    MemoryType.MALADAPTIVE_COPING,
                    MemoryType.ADAPTIVE_COPING,
                    MemoryType.SYMPTOM,
                    MemoryType.TRIGGER,
                    MemoryType.STRESSOR,
                    MemoryType.FEAR,
                    MemoryType.THERAPY_TOPIC,
                    MemoryType.THERAPY_GOAL
            );

            case "LEGAL" -> isAnyOf(
                    type,
                    MemoryType.LIFE_EVENT,
                    MemoryType.STRESSOR,
                    MemoryType.FEAR,
                    MemoryType.TRIGGER,
                    MemoryType.THERAPY_TOPIC,
                    MemoryType.THERAPY_GOAL
            );

            default -> false;
        };
    }

    private static boolean isAnyOf(
            MemoryType actual,
            MemoryType... expected
    ) {
        if (actual == null || expected == null) {
            return false;
        }

        for (MemoryType candidate : expected) {
            if (actual == candidate) {
                return true;
            }
        }

        return false;
    }

    private static double clamp(double value) {
        return Math.clamp(value, 0.0, 1.0);
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }

        return value
                .toLowerCase(Locale.ROOT)
                .replace('_', ' ')
                .replace('-', ' ')
                .strip();
    }
}