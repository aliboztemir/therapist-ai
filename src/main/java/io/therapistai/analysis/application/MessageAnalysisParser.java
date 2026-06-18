package io.therapistai.analysis.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.therapistai.analysis.domain.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MessageAnalysisParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public MessageAnalysis parse(String rawResponse) {
        if (rawResponse == null || rawResponse.isBlank()) {
            return MessageAnalysis.safeDefault("empty-response");
        }

        try {
            JsonNode root = objectMapper.readTree(extractJson(rawResponse));
            return toMessageAnalysis(root);
        } catch (Exception ex) {
            return MessageAnalysis.safeDefault("parse-error");
        }
    }

    private static MessageAnalysis toMessageAnalysis(JsonNode root) {
        return MessageAnalysis.builder()
                .messageType(enumValue(root, "messageType", MessageType.class, MessageType.UNKNOWN))
                .userIntent(enumValue(root, "userIntent", UserIntent.class, UserIntent.UNKNOWN))
                .primaryEmotion(enumValue(root, "primaryEmotion", EmotionType.class, EmotionType.UNKNOWN))
                .secondaryEmotion(enumValue(root, "secondaryEmotion", EmotionType.class, EmotionType.UNKNOWN))
                .sentiment(enumValue(root, "sentiment", SentimentType.class, SentimentType.UNKNOWN))
                .emotionalIntensity(clampInt(root, "emotionalIntensity", 0, 10, 0))
                .themes(enumList(root, "themes", MessageTheme.class))
                .temporalFocus(enumValue(root, "temporalFocus", TemporalFocus.class, TemporalFocus.UNKNOWN))
                .communicationStyles(enumList(root, "communicationStyles", CommunicationStyle.class))
                .cognitiveSignals(enumList(root, "cognitiveSignals", CognitiveSignal.class))
                .disclosureLevel(enumValue(root, "disclosureLevel", DisclosureLevel.class, DisclosureLevel.MINIMAL))
                .adviceSeeking(booleanValue(root, "adviceSeeking"))
                .boundaryTest(booleanValue(root, "boundaryTest"))
                .crisisSignalDetected(booleanValue(root, "crisisSignalDetected"))
                .confidence(clampDouble(root, "confidence", 0.0, 1.0, 0.0))
                .build();
    }

    private static String extractJson(String raw) {
        String stripped = raw.strip();

        if (stripped.startsWith("```")) {
            int firstLineBreak = stripped.indexOf('\n');
            int closingFence = stripped.lastIndexOf("```");

            if (firstLineBreak >= 0 && closingFence > firstLineBreak) {
                stripped = stripped.substring(firstLineBreak + 1, closingFence).strip();
            }
        }

        int start = stripped.indexOf('{');
        int end = stripped.lastIndexOf('}');

        if (start >= 0 && end > start) {
            return stripped.substring(start, end + 1);
        }

        return stripped;
    }

    private static <E extends Enum<E>> E enumValue(
            JsonNode root,
            String field,
            Class<E> type,
            E fallback
    ) {
        String raw = readText(root, field);

        if (raw == null) {
            return fallback;
        }

        try {
            return Enum.valueOf(type, normalizeEnumToken(raw));
        } catch (Exception ignored) {
            return fallback;
        }
    }

    private static <E extends Enum<E>> List<E> enumList(
            JsonNode root,
            String field,
            Class<E> type
    ) {
        JsonNode array = root.get(field);

        if (array == null || !array.isArray()) {
            return List.of();
        }

        List<E> values = new ArrayList<>();

        for (JsonNode item : array) {
            if (item == null || !item.isTextual()) {
                continue;
            }

            try {
                values.add(Enum.valueOf(type, normalizeEnumToken(item.asText())));
            } catch (Exception ignored) {
            }
        }

        return values;
    }

    private static String readText(JsonNode root, String field) {
        JsonNode value = root.get(field);

        if (value == null || value.isNull()) {
            return null;
        }

        if (value.isArray() && !value.isEmpty()) {
            value = value.get(0);
        }

        if (!value.isTextual()) {
            return null;
        }

        return value.asText();
    }

    private static boolean booleanValue(JsonNode root, String field) {
        JsonNode value = root.get(field);
        return value != null && !value.isNull() && value.asBoolean(false);
    }

    private static int clampInt(
            JsonNode root,
            String field,
            int min,
            int max,
            int fallback
    ) {
        JsonNode value = root.get(field);

        if (value == null || value.isNull()) {
            return fallback;
        }

        return Math.clamp(max, min, value.asInt(fallback));
    }

    private static double clampDouble(
            JsonNode root,
            String field,
            double min,
            double max,
            double fallback
    ) {
        JsonNode value = root.get(field);

        if (value == null || value.isNull()) {
            return fallback;
        }

        return Math.clamp(max, min, value.asDouble(fallback));
    }

    private static String normalizeEnumToken(String raw) {
        if (raw == null) {
            return "";
        }

        return raw.trim()
                .toUpperCase()
                .replace('-', '_')
                .replace(' ', '_');
    }
}