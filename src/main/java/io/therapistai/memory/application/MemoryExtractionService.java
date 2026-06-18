package io.therapistai.memory.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.therapistai.ai.application.AIProviderGateway;
import io.therapistai.ai.domain.AIRequest;
import io.therapistai.ai.domain.AIResponse;
import io.therapistai.memory.domain.MemoryCandidate;
import io.therapistai.memory.domain.MemoryKey;
import io.therapistai.memory.domain.MemoryType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.regex.Pattern;

@Service
class MemoryExtractionService {

    private static final Logger log =
            LoggerFactory.getLogger(MemoryExtractionService.class);

    private static final Pattern MALFORMED_CONFIDENCE_PATTERN = Pattern.compile(
            "\"confidence\"\\s*:\\s*\\d+\\.\\s+[a-zA-Z][a-zA-Z0-9]*"
    );

    private static final String SYSTEM_PROMPT = """
            You are a structured memory extraction engine for a mental health support application.
            
            Your only task is to extract stable, reusable, clinically or personally relevant memory candidates from the user's CURRENT MESSAGE only.
            
            This is NOT a therapy response task.
            Do NOT provide advice.
            Do NOT provide emotional support.
            Do NOT generate conversational text.
            Do NOT diagnose.
            Do NOT explain your reasoning.
            
            Extract only information that is explicitly stated in the CURRENT MESSAGE.
            Do not extract facts, goals, symptoms, coping patterns, fears, stressors, preferences, or expectations from previous messages.
            Do not use recent history as evidence.
            Do not infer or complete missing information from previous messages.
            Message analysis context may only be used to choose between allowed memory types and keys when the CURRENT MESSAGE already contains explicit evidence.
            If a value is not explicitly present in the CURRENT MESSAGE, omit that item.
            
            Do not invent dates, durations, relationships, diagnoses, motivations, or statuses.
            Be conservative. Prefer fewer high-confidence items over many weak items.
            
            Return ONLY a raw JSON array.
            Do NOT use markdown.
            Do NOT wrap the JSON in code fences.
            If nothing memory-worthy exists, return [].
            
            Required output format:
            [
              {
                "type": "WORK_STATUS",
                "key": "EMPLOYMENT_STATUS",
                "value": "unemployed",
                "confidence": 0.95,
                "metadata": {
                  "evidenceText": "Yaklaşık dört aydır işsizim."
                }
              }
            ]
            
            Every item MUST contain:
            - type
            - key
            - value
            - confidence
            - metadata.evidenceText
            
            metadata.evidenceText MUST be an exact substring from the CURRENT MESSAGE.
            Never use evidenceText from recent history, assistant messages, inferred context, or analysis context.
            
            Do not include source, reason, category, label, diagnosis, explanation, normalizedValue, evidenceType, timestamps, ids, or action fields.
            
            Allowed memory types and keys:
            
            PERSON:
            - NAME
            - AGE
            - DATE_OF_BIRTH
            - GENDER
            - NATIONALITY
            - LANGUAGE
            - ROLE
            - DESCRIPTION
            
            WORK_STATUS:
            - EMPLOYMENT_STATUS
            - JOB_TITLE
            - COMPANY
            - INDUSTRY
            - WORK_MODE
            - UNEMPLOYMENT_DURATION
            - JOB_SEARCH_STATUS
            - WORK_STRESS
            - CAREER_CONCERN
            
            SCHOOL_STATUS:
            - EDUCATION_STATUS
            - SCHOOL_NAME
            - FIELD_OF_STUDY
            - ACADEMIC_LEVEL
            - ACADEMIC_CONCERN
            
            FINANCIAL_STATUS:
            - FINANCIAL_SITUATION
            - DEBT_STATUS
            - INCOME_STATUS
            - FINANCIAL_CONCERN
            - FINANCIAL_DEPENDENCY
            
            LIVING_SITUATION:
            - COUNTRY
            - CITY
            - HOUSEHOLD
            - LIVING_ARRANGEMENT
            - HOUSING_CONCERN
            
            RELATIONSHIP_STATUS:
            - MARITAL_STATUS
            - PARTNERSHIP_STATUS
            - RELATIONSHIP_QUALITY
            - CURRENT_CONFLICT
            - SEPARATION_STATUS
            
            SUPPORT_SYSTEM:
            - SUPPORT_PERSON
            - SUPPORT_ROLE
            - SUPPORT_TYPE
            - SUPPORT_AVAILABILITY
            
            PREFERENCE:
            - PREFERENCE_TYPE
            - PREFERENCE_VALUE
            
            LIFE_EVENT:
            - EVENT_TYPE
            - EVENT_DATE
            - DESCRIPTION
            - IMPACT
            - STATUS
            
            CHILDHOOD_BACKGROUND:
            - FAMILY_STRUCTURE
            - PARENT_RELATIONSHIP
            - CHILDHOOD_EVENT
            - CHILDHOOD_DESCRIPTION
            
            CORE_BELIEF:
            - BELIEF
            - BELIEF_DOMAIN
            
            IDENTITY:
            - IDENTITY_STATEMENT
            - IDENTITY_DOMAIN
            - SELF_DESCRIPTION
            
            THERAPY_GOAL:
            - GOAL
            - GOAL_PRIORITY
            - GOAL_PROGRESS
            
            THERAPY_TOPIC:
            - TOPIC
            - TOPIC_FREQUENCY
            - TOPIC_IMPORTANCE
            
            THERAPY_EXPECTATION:
            - EXPECTATION
            - EXPECTATION_PRIORITY
            
            ADAPTIVE_COPING:
            - COPING_STRATEGY
            - COPING_EFFECTIVENESS
            - COPING_FREQUENCY
            
            MALADAPTIVE_COPING:
            - COPING_STRATEGY
            - COPING_CONSEQUENCE
            - COPING_FREQUENCY
            
            SYMPTOM:
            - SYMPTOM_NAME
            - SEVERITY
            - FREQUENCY
            - DURATION
            
            TRIGGER:
            - TRIGGER_SOURCE
            - TRIGGER_CONTEXT
            - TRIGGER_INTENSITY
            
            FEAR:
            - FEAR_OBJECT
            - FEAR_CONTEXT
            - FEAR_INTENSITY
            
            STRESSOR:
            - STRESSOR_SOURCE
            - STRESSOR_CONTEXT
            - STRESS_LEVEL
            
            Key selection rules:
            - Never invent a memory type.
            - Never invent a memory key.
            - The type and key values must exactly match the uppercase enum names listed above.
            - Always select the closest allowed key for the selected type.
            - If no allowed key fits, do not extract that item.
            - Temporary emotion alone is not enough for memory.
            - A symptom, trigger, fear, coping pattern, or stressor should be extracted only when it is explicit, repeated, intense, or functionally important in the CURRENT MESSAGE.
            - Do not classify user wording as a clinical diagnosis.
            - If the user says "depresyona soktu", "depresif", or similar, extract it as a reported depressive state, not as a diagnosis.
            
            Examples:
            
            Message: "Yaklaşık dört aydır işsizim."
            Output:
            [{"type":"WORK_STATUS","key":"EMPLOYMENT_STATUS","value":"unemployed","confidence":0.95,"metadata":{"evidenceText":"işsizim"}},{"type":"WORK_STATUS","key":"UNEMPLOYMENT_DURATION","value":"about four months","confidence":0.92,"metadata":{"evidenceText":"Yaklaşık dört aydır"}}]
            
            Message: "Ciddi ekonomik sorunlarım var, borçla geçiniyorum."
            Output:
            [{"type":"FINANCIAL_STATUS","key":"FINANCIAL_SITUATION","value":"experiencing serious financial problems","confidence":0.94,"metadata":{"evidenceText":"Ciddi ekonomik sorunlarım var"}},{"type":"FINANCIAL_STATUS","key":"DEBT_STATUS","value":"living on debt","confidence":0.93,"metadata":{"evidenceText":"borçla geçiniyorum"}}]
            
            Message: "Eşim ve çocuğumla İspanya'da yaşıyorum."
            Output:
            [{"type":"LIVING_SITUATION","key":"COUNTRY","value":"Spain","confidence":0.95,"metadata":{"evidenceText":"İspanya'da yaşıyorum"}},{"type":"LIVING_SITUATION","key":"HOUSEHOLD","value":"lives with spouse and child","confidence":0.94,"metadata":{"evidenceText":"Eşim ve çocuğumla"}},{"type":"RELATIONSHIP_STATUS","key":"MARITAL_STATUS","value":"married","confidence":0.88,"metadata":{"evidenceText":"Eşim"}}]
            
            Message: "Başarısız olursam değersiz biri gibi hissediyorum."
            Output:
            [{"type":"CORE_BELIEF","key":"BELIEF","value":"personal worth is strongly linked to success","confidence":0.84,"metadata":{"evidenceText":"Başarısız olursam değersiz biri gibi hissediyorum"}},{"type":"CORE_BELIEF","key":"BELIEF_DOMAIN","value":"self-worth and achievement","confidence":0.80,"metadata":{"evidenceText":"Başarısız olursam değersiz"}}]
            
            Message: "Kaygılıyım."
            Output:
            []
            
            Message: "Son iki aydır neredeyse her gün kaygı yaşıyorum."
            Output:
            [{"type":"SYMPTOM","key":"SYMPTOM_NAME","value":"anxiety","confidence":0.92,"metadata":{"evidenceText":"kaygı yaşıyorum"}},{"type":"SYMPTOM","key":"DURATION","value":"about two months","confidence":0.90,"metadata":{"evidenceText":"Son iki aydır"}},{"type":"SYMPTOM","key":"FREQUENCY","value":"almost every day","confidence":0.90,"metadata":{"evidenceText":"neredeyse her gün"}}]
            
            Message: "kendimi iyi hissetmiyorum 4 aydir. issizlik hali beni feci depresyona soktu"
            Output:
            [{"type":"SYMPTOM","key":"SYMPTOM_NAME","value":"reported depressive state","confidence":0.90,"metadata":{"evidenceText":"depresyona soktu"}},{"type":"SYMPTOM","key":"DURATION","value":"about four months","confidence":0.90,"metadata":{"evidenceText":"4 aydir"}},{"type":"WORK_STATUS","key":"EMPLOYMENT_STATUS","value":"unemployed","confidence":0.90,"metadata":{"evidenceText":"issizlik hali"}}]
            
            Message: "Merhaba, nasılsın?"
            Output:
            []
            """;

    private final AIProviderGateway aiGateway;
    private final ObjectMapper objectMapper;

    public MemoryExtractionService(
            AIProviderGateway aiGateway,
            ObjectMapper objectMapper
    ) {
        this.aiGateway = Objects.requireNonNull(aiGateway, "aiGateway must not be null");
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper must not be null");
    }

    public List<MemoryCandidate> extract(
            String currentMessage,
            MemorySignalContext signalContext
    ) {
        if (currentMessage == null || currentMessage.isBlank()) {
            return List.of();
        }

        try {
            String prompt = buildPrompt(
                    currentMessage,
                    signalContext
            );

            AIResponse response =
                    aiGateway.generate(
                            new AIRequest(prompt)
                    );

            List<MemoryCandidate> candidates =
                    parseResponse(response.content());

            log.debug(
                    "memory.extraction.completed candidates={}",
                    candidates.size()
            );

            return candidates;

        } catch (Exception ex) {
            log.warn(
                    "memory.extraction.failed error={}",
                    ex.getMessage(),
                    ex
            );
            return List.of();
        }
    }

    private static String buildPrompt(
            String currentMessage,
            MemorySignalContext signalContext
    ) {
        return """
                %s
                
                %s
                """.formatted(
                SYSTEM_PROMPT,
                buildUserPrompt(
                        currentMessage,
                        signalContext
                )
        ).strip();
    }

    private static String buildUserPrompt(
            String currentMessage,
            MemorySignalContext signalContext
    ) {
        StringBuilder sb = new StringBuilder();

        MemorySignalContext effectiveSignalContext =
                signalContext != null ? signalContext : MemorySignalContext.empty();

        sb.append("Message analysis context:\n");
        sb.append("messageType=").append(effectiveSignalContext.messageType()).append("\n");
        sb.append("userIntent=").append(effectiveSignalContext.userIntent()).append("\n");
        sb.append("themes=").append(effectiveSignalContext.themes()).append("\n");
        sb.append("primaryEmotion=").append(effectiveSignalContext.primaryEmotion()).append("\n");
        sb.append("secondaryEmotion=").append(effectiveSignalContext.secondaryEmotion()).append("\n");
        sb.append("sentiment=").append(effectiveSignalContext.sentiment()).append("\n");
        sb.append("emotionalIntensity=").append(effectiveSignalContext.emotionalIntensity()).append("\n");
        sb.append("cognitiveSignals=").append(effectiveSignalContext.cognitiveSignals()).append("\n");
        sb.append("disclosureLevel=").append(effectiveSignalContext.disclosureLevel()).append("\n\n");

        sb.append("Current message to extract memory from:\n");
        sb.append(currentMessage);
        sb.append("\n\nReturn only the JSON array.");

        return sb.toString();
    }

    private List<MemoryCandidate> parseResponse(String raw) {
        if (raw == null || raw.isBlank()) {
            return List.of();
        }

        try {
            JsonNode root = objectMapper.readTree(extractJsonArray(raw));

            if (!root.isArray()) {
                return List.of();
            }

            List<MemoryCandidate> result = new ArrayList<>();

            for (JsonNode node : root) {
                MemoryCandidate candidate = parseCandidate(node);

                if (candidate != null && candidate.isValid()) {
                    result.add(candidate);
                }
            }

            return result;

        } catch (Exception ex) {
            log.debug("memory.extraction.parse.failed error={}", ex.getMessage());
            return List.of();
        }
    }

    private static String extractJsonArray(String raw) {
        String value = raw.strip();

        if (value.startsWith("```")) {
            int newline = value.indexOf('\n');
            int closing = value.lastIndexOf("```");

            if (newline >= 0 && closing > newline) {
                value = value.substring(newline + 1, closing).strip();
            }
        }

        int start = value.indexOf('[');
        int end = value.lastIndexOf(']');

        if (start >= 0 && end > start) {
            value = value.substring(start, end + 1);
        }

        return MALFORMED_CONFIDENCE_PATTERN
                .matcher(value)
                .replaceAll("\"confidence\": 0.5");
    }

    private static MemoryCandidate parseCandidate(JsonNode node) {
        try {
            JsonNode typeNode = node.get("type");
            JsonNode keyNode = node.get("key");
            JsonNode valueNode = node.get("value");

            if (typeNode == null || keyNode == null || valueNode == null) {
                return null;
            }

            String value = valueNode.asText("").strip();

            if (value.isBlank()) {
                return null;
            }

            MemoryType type = parseMemoryType(typeNode.asText());
            MemoryKey key = parseMemoryKey(keyNode.asText());

            if (type == null || key == null || !type.allows(key)) {
                return null;
            }

            return MemoryCandidate.of(
                    type,
                    key,
                    value,
                    parseConfidence(node),
                    parseMetadata(node)
            );

        } catch (Exception ex) {
            return null;
        }
    }

    private static Map<String, Object> parseMetadata(JsonNode node) {
        JsonNode metadataNode = node.get("metadata");

        if (metadataNode == null || metadataNode.isNull() || !metadataNode.isObject()) {
            return Map.of();
        }

        JsonNode evidenceNode = metadataNode.get("evidenceText");

        if (evidenceNode == null || evidenceNode.isNull()) {
            return Map.of();
        }

        String evidenceText = evidenceNode.asText("").strip();

        if (evidenceText.isBlank()) {
            return Map.of();
        }

        return Map.of("evidenceText", evidenceText);
    }

    private static MemoryType parseMemoryType(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }

        try {
            return MemoryType.valueOf(normalizeEnum(raw));
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private static MemoryKey parseMemoryKey(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }

        try {
            return MemoryKey.valueOf(normalizeEnum(raw));
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private static String normalizeEnum(String raw) {
        return raw.strip()
                .toUpperCase()
                .replace('-', '_')
                .replace(' ', '_');
    }

    private static double parseConfidence(JsonNode node) {
        JsonNode confidenceNode = node.get("confidence");

        if (confidenceNode == null || confidenceNode.isNull()) {
            return 0.5;
        }

        if (confidenceNode.isNumber()) {
            return clampConfidence(confidenceNode.asDouble(0.5));
        }

        try {
            return clampConfidence(
                    Double.parseDouble(confidenceNode.asText().strip())
            );
        } catch (NumberFormatException ex) {
            return 0.5;
        }
    }

    private static double clampConfidence(double value) {
        return Math.clamp(value, 0.0, 1.0);
    }
}