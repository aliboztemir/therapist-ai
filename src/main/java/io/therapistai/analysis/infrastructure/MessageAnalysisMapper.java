package io.therapistai.analysis.infrastructure;

import io.therapistai.analysis.domain.MessageAnalysis;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

final class MessageAnalysisMapper {

    private MessageAnalysisMapper() {
    }

    static MessageAnalysisEntity toEntity(
            UUID analysisId,
            UUID messageId,
            UUID userId,
            UUID conversationId,
            MessageAnalysis analysis,
            Instant createdAt
    ) {
        if (analysis == null) {
            analysis = MessageAnalysis.safeDefault("null-analysis");
        }

        return new MessageAnalysisEntity(
                analysisId,
                messageId,
                userId,
                conversationId,
                analysis.messageType(),
                analysis.userIntent(),
                analysis.primaryEmotion(),
                analysis.secondaryEmotion(),
                analysis.sentiment(),
                analysis.emotionalIntensity(),
                analysis.themes() != null
                        ? new HashSet<>(analysis.themes())
                        : new HashSet<>(),
                analysis.temporalFocus(),
                analysis.communicationStyles() != null
                        ? new HashSet<>(analysis.communicationStyles())
                        : new HashSet<>(),
                analysis.cognitiveSignals() != null
                        ? new HashSet<>(analysis.cognitiveSignals())
                        : new HashSet<>(),
                analysis.disclosureLevel(),
                analysis.isAdviceSeeking(),
                analysis.isBoundaryTest(),
                analysis.isCrisisSignalDetected(),
                analysis.confidence(),
                analysis.fallbackReason(),
                createdAt
        );
    }

    static MessageAnalysis toDomain(MessageAnalysisEntity entity) {
        if (entity == null) {
            return MessageAnalysis.safeDefault("null-entity");
        }

        return MessageAnalysis.builder()
                .messageType(entity.getMessageType())
                .userIntent(entity.getUserIntent())
                .primaryEmotion(entity.getPrimaryEmotion())
                .secondaryEmotion(entity.getSecondaryEmotion())
                .sentiment(entity.getSentiment())
                .emotionalIntensity(entity.getEmotionalIntensity())
                .themes(
                        entity.getThemes() != null
                                ? List.copyOf(entity.getThemes())
                                : List.of()
                )
                .temporalFocus(entity.getTemporalFocus())
                .communicationStyles(
                        entity.getCommunicationStyles() != null
                                ? List.copyOf(entity.getCommunicationStyles())
                                : List.of()
                )
                .cognitiveSignals(
                        entity.getCognitiveSignals() != null
                                ? List.copyOf(entity.getCognitiveSignals())
                                : List.of()
                )
                .disclosureLevel(entity.getDisclosureLevel())
                .adviceSeeking(entity.isAdviceSeeking())
                .boundaryTest(entity.isBoundaryTest())
                .crisisSignalDetected(entity.isCrisisSignalDetected())
                .confidence(entity.getConfidence())
                .fallbackReason(entity.getFallbackReason())
                .build();
    }
}