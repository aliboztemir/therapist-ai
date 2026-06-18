package io.therapistai.analysis.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.therapistai.analysis.domain.CognitiveSignal;
import io.therapistai.analysis.domain.CommunicationStyle;
import io.therapistai.analysis.domain.DisclosureLevel;
import io.therapistai.analysis.domain.EmotionType;
import io.therapistai.analysis.domain.MessageTheme;
import io.therapistai.analysis.domain.MessageType;
import io.therapistai.analysis.domain.SentimentType;
import io.therapistai.analysis.domain.TemporalFocus;
import io.therapistai.analysis.domain.UserIntent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class MessageAnalysisPromptBuilder {

    private static final String INSTRUCTION = """
            You are the message analysis engine of an AI psychotherapy application.
            
            Analyze the latest user message and return structured JSON only.
            
            The current message is the primary source of truth.
            
            Recent history may only be used to:
            - resolve references,
            - resolve pronouns,
            - understand explicit continuation of a previously discussed topic.
            
            Never inherit themes, emotions, cognitiveSignals, communicationStyles, or crisis indicators from history unless the current message explicitly refers to them.
            
            Classify only what is explicitly stated or strongly implied.
            
            Do not infer diagnoses, personality traits, long-term beliefs, or assessment conclusions.
            
            Message type rules:
            
            - FACT = objective personal information.
            - LIFE_EVENT = significant life event.
            - EMOTIONAL_DISCLOSURE = emotional state, distress, fear, worry, suffering.
            - GOAL_STATEMENT = desired change or objective.
            - PREFERENCE = preference, boundary, communication choice.
            - QUESTION = explicit question.
            - CRISIS = explicit self-harm, suicide, violence risk, inability to stay safe.
            - GENERAL = understandable but does not fit another category.
            - UNKNOWN = cannot be determined.
            
            Use GENERAL only when no specific type applies.
            
            Secondary emotion is optional.
            Use UNKNOWN when no clear secondary emotion exists.
            
            Themes:
            - Assign only when directly present.
            - Prefer empty arrays over weak inferences.
            
            Communication styles:
            - Use only observable styles.
            - Prefer one primary style.
            
            Cognitive signals:
            - Require direct linguistic evidence.
            - Never infer from emotion alone.
            - Prefer empty arrays over weak inferences.
            
            crisisSignalDetected:
            - true only for explicit or strongly implied immediate safety risk.
            - never true for anxiety, shame, guilt, self-criticism, hopelessness, perfectionism, catastrophizing, or distress alone.
            
            Use UNKNOWN only when a value cannot be reliably determined.
            
            Return valid JSON only.
            """;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public AnalysisPrompt build(AnalysisInput input) {
        Objects.requireNonNull(input, "input must not be null");

        Map<String, Object> root = new LinkedHashMap<>();
        root.put("instruction", INSTRUCTION.strip());
        root.put("input", buildInput(input));
        root.put("outputSchema", buildOutputSchema());

        try {
            return new AnalysisPrompt(objectMapper.writeValueAsString(root));
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to build message analysis prompt.", ex);
        }
    }

    private static Map<String, Object> buildInput(AnalysisInput input) {
        Map<String, Object> inputObject = new LinkedHashMap<>();
        inputObject.put("currentMessage", input.currentMessage());
        inputObject.put("recentHistory", toRecentHistory(input.recentHistory()));
        return inputObject;
    }

    private static List<Map<String, String>> toRecentHistory(List<AnalysisInput.AnalysisHistoryMessage> history) {
        if (history == null || history.isEmpty()) {
            return List.of();
        }

        List<Map<String, String>> output = new ArrayList<>();

        for (AnalysisInput.AnalysisHistoryMessage message : history) {
            if (message == null) {
                continue;
            }

            Map<String, String> item = new LinkedHashMap<>();
            item.put("role", toPromptRole(message.role()));
            item.put("content", message.content() != null ? message.content() : "");
            output.add(item);
        }

        return output;
    }

    private static String toPromptRole(String role) {
        if (role == null || role.isBlank()) {
            return "unknown";
        }

        return switch (role.trim().toUpperCase()) {
            case "USER" -> "user";
            case "ASSISTANT" -> "assistant";
            default -> role.trim().toLowerCase();
        };
    }

    private static Map<String, Object> buildOutputSchema() {
        Map<String, Object> schema = new LinkedHashMap<>();

        schema.put("messageType", enumValues(MessageType.values()));
        schema.put("userIntent", enumValues(UserIntent.values()));
        schema.put("primaryEmotion", enumValues(EmotionType.values()));
        schema.put("secondaryEmotion", enumValues(EmotionType.values()));
        schema.put("sentiment", enumValues(SentimentType.values()));
        schema.put("emotionalIntensity", "integer 0-10");
        schema.put("themes", enumValues(MessageTheme.values()));
        schema.put("temporalFocus", enumValues(TemporalFocus.values()));
        schema.put("communicationStyles", enumValues(CommunicationStyle.values()));
        schema.put("cognitiveSignals", enumValues(CognitiveSignal.values()));
        schema.put("disclosureLevel", enumValues(DisclosureLevel.values()));
        schema.put("adviceSeeking", "boolean");
        schema.put("boundaryTest", "boolean");
        schema.put("crisisSignalDetected", "boolean");
        schema.put("confidence", "number 0.0-1.0");

        return schema;
    }

    private static List<String> enumValues(Enum<?>[] values) {
        List<String> output = new ArrayList<>();

        for (Enum<?> value : values) {
            output.add(value.name());
        }

        return output;
    }
}