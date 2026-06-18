package io.therapistai.analysis.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataMessageAnalysisRepository
        extends JpaRepository<MessageAnalysisEntity, UUID> {

    Optional<MessageAnalysisEntity> findByMessageId(UUID messageId);
}