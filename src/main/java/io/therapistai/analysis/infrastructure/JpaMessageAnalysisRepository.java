package io.therapistai.analysis.infrastructure;

import io.therapistai.analysis.application.MessageAnalysisRepository;
import io.therapistai.analysis.domain.MessageAnalysis;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaMessageAnalysisRepository
        implements MessageAnalysisRepository {

    private final SpringDataMessageAnalysisRepository jpaRepository;

    public JpaMessageAnalysisRepository(
            SpringDataMessageAnalysisRepository jpaRepository
    ) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(
            UUID userId,
            UUID conversationId,
            UUID messageId,
            MessageAnalysis analysis
    ) {
        MessageAnalysisEntity entity =
                MessageAnalysisMapper.toEntity(
                        UUID.randomUUID(),
                        messageId,
                        userId,
                        conversationId,
                        analysis,
                        Instant.now()
                );

        jpaRepository.save(entity);
    }
}