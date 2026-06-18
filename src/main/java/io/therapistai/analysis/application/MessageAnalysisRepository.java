package io.therapistai.analysis.application;

import io.therapistai.analysis.domain.MessageAnalysis;

import java.util.Optional;
import java.util.UUID;

public interface MessageAnalysisRepository {

    void save(
            UUID userId,
            UUID conversationId,
            UUID messageId,
            MessageAnalysis analysis
    );
}