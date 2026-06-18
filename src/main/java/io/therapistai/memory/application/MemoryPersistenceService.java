package io.therapistai.memory.application;

import io.therapistai.memory.domain.MemoryCandidate;
import io.therapistai.memory.domain.MemoryImportance;
import io.therapistai.memory.domain.MemoryItem;
import io.therapistai.memory.domain.MemoryRepository;
import io.therapistai.memory.domain.MemoryType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
class MemoryPersistenceService {

    private static final Logger log =
            LoggerFactory.getLogger(MemoryPersistenceService.class);

    private static final double PERSIST_CONFIDENCE_THRESHOLD = 0.75;

    private final MemoryRepository memoryRepository;

    public MemoryPersistenceService(MemoryRepository memoryRepository) {
        this.memoryRepository = memoryRepository;
    }

    public void persist(
            UUID userId,
            UUID conversationId,
            UUID messageId,
            List<MemoryCandidate> candidates
    ) {
        if (userId == null || conversationId == null || messageId == null) {
            log.warn(
                    "memory.persist.skipped reason=missing_required_ids userId={} conversationId={} messageId={}",
                    userId,
                    conversationId,
                    messageId
            );
            return;
        }

        if (candidates == null || candidates.isEmpty()) {
            log.debug(
                    "memory.persist.skipped reason=no_candidates userId={} conversationId={} messageId={}",
                    userId,
                    conversationId,
                    messageId
            );
            return;
        }

        Instant now = Instant.now();

        int saved = 0;
        int skipped = 0;
        int failed = 0;

        for (MemoryCandidate candidate : candidates) {
            if (!shouldPersist(candidate)) {
                skipped++;

                log.debug(
                        "memory.persist.skipped reason=invalid_candidate userId={} conversationId={} messageId={} type={} key={} value={} confidence={} valid={}",
                        userId,
                        conversationId,
                        messageId,
                        candidate != null ? candidate.type() : null,
                        candidate != null ? candidate.key() : null,
                        candidate != null ? candidate.value() : null,
                        candidate != null ? candidate.confidence() : null,
                        candidate != null && candidate.isValid()
                );

                continue;
            }

            try {
                boolean persisted =
                        persistCandidate(
                                userId,
                                conversationId,
                                messageId,
                                candidate,
                                now
                        );

                if (persisted) {
                    saved++;
                } else {
                    skipped++;
                }

            } catch (Exception ex) {
                failed++;

                log.warn(
                        "memory.persist.failed userId={} conversationId={} messageId={} type={} key={} error={}",
                        userId,
                        conversationId,
                        messageId,
                        candidate.type(),
                        candidate.key(),
                        ex.getMessage(),
                        ex
                );
            }
        }

        log.debug(
                "memory.persist.complete userId={} conversationId={} messageId={} candidates={} saved={} skipped={} failed={}",
                userId,
                conversationId,
                messageId,
                candidates.size(),
                saved,
                skipped,
                failed
        );
    }

    private boolean shouldPersist(MemoryCandidate candidate) {
        return candidate != null
                && candidate.isValid()
                && candidate.confidence() >= PERSIST_CONFIDENCE_THRESHOLD;
    }

    private boolean persistCandidate(
            UUID userId,
            UUID conversationId,
            UUID messageId,
            MemoryCandidate candidate,
            Instant now
    ) {
        MemoryType type = candidate.type();

        return switch (type.constraintType()) {
            case UNIQUE -> persistUnique(
                    userId,
                    conversationId,
                    messageId,
                    candidate,
                    now
            );

            case ACCUMULATIVE -> persistAccumulative(
                    userId,
                    conversationId,
                    messageId,
                    candidate,
                    now
            );

            case EVOLUTIONARY -> persistEvolutionary(
                    userId,
                    conversationId,
                    messageId,
                    candidate,
                    now
            );
        };
    }

    private boolean persistUnique(
            UUID userId,
            UUID conversationId,
            UUID messageId,
            MemoryCandidate candidate,
            Instant now
    ) {
        Optional<MemoryItem> active =
                memoryRepository.findActiveByUserIdAndTypeAndKey(
                        userId,
                        candidate.type(),
                        candidate.key()
                );

        if (active.isPresent()
                && sameValue(active.get().value(), candidate.value())) {
            log.debug(
                    "memory.persist.skipped reason=same_value userId={} type={} key={} value={}",
                    userId,
                    candidate.type(),
                    candidate.key(),
                    candidate.value()
            );
            return false;
        }

        active.ifPresent(memory -> memoryRepository.save(memory.archived()));

        MemoryItem newItem = newMemoryItem(
                userId,
                conversationId,
                messageId,
                candidate,
                1,
                null,
                now
        );

        memoryRepository.save(newItem);
        return true;
    }

    private boolean persistAccumulative(
            UUID userId,
            UUID conversationId,
            UUID messageId,
            MemoryCandidate candidate,
            Instant now
    ) {
        MemoryItem newItem = newMemoryItem(
                userId,
                conversationId,
                messageId,
                candidate,
                1,
                null,
                now
        );

        memoryRepository.save(newItem);
        return true;
    }

    private boolean persistEvolutionary(
            UUID userId,
            UUID conversationId,
            UUID messageId,
            MemoryCandidate candidate,
            Instant now
    ) {
        Optional<MemoryItem> active =
                memoryRepository.findActiveByUserIdAndTypeAndKey(
                        userId,
                        candidate.type(),
                        candidate.key()
                );

        if (active.isEmpty()) {
            MemoryItem firstVersion = newMemoryItem(
                    userId,
                    conversationId,
                    messageId,
                    candidate,
                    1,
                    null,
                    now
            );

            memoryRepository.save(firstVersion);
            return true;
        }

        MemoryItem current = active.get();

        if (sameValue(current.value(), candidate.value())) {
            log.debug(
                    "memory.persist.skipped reason=same_value userId={} type={} key={} value={}",
                    userId,
                    candidate.type(),
                    candidate.key(),
                    candidate.value()
            );
            return false;
        }

        memoryRepository.save(current.archived());

        MemoryItem nextVersion = current.nextVersion(
                UUID.randomUUID(),
                candidate.value(),
                candidate.confidence(),
                defaultImportance(candidate.type()),
                conversationId,
                messageId,
                candidate.metadata(),
                now
        );

        memoryRepository.save(nextVersion);
        return true;
    }

    private static MemoryItem newMemoryItem(
            UUID userId,
            UUID conversationId,
            UUID messageId,
            MemoryCandidate candidate,
            int version,
            UUID parentMemoryId,
            Instant now
    ) {
        return MemoryItem.builder()
                .userId(userId)
                .type(candidate.type())
                .key(candidate.key())
                .value(candidate.value())
                .confidence(candidate.confidence())
                .importance(defaultImportance(candidate.type()))
                .version(version)
                .parentMemoryId(parentMemoryId)
                .conversationId(conversationId)
                .messageId(messageId)
                .metadata(candidate.metadata())
                .createdAt(now)
                .build();
    }

    private static boolean sameValue(
            String existingValue,
            String candidateValue
    ) {
        if (existingValue == null || candidateValue == null) {
            return false;
        }

        return existingValue.trim()
                .equalsIgnoreCase(candidateValue.trim());
    }

    private static int defaultImportance(MemoryType type) {
        return switch (type) {
            case CORE_BELIEF,
                 IDENTITY,
                 LIFE_EVENT,
                 CHILDHOOD_BACKGROUND,
                 THERAPY_GOAL,
                 MALADAPTIVE_COPING,
                 SYMPTOM,
                 TRIGGER,
                 FEAR,
                 STRESSOR -> MemoryImportance.HIGH.score();

            case WORK_STATUS,
                 SCHOOL_STATUS,
                 FINANCIAL_STATUS,
                 LIVING_SITUATION,
                 RELATIONSHIP_STATUS,
                 SUPPORT_SYSTEM,
                 THERAPY_TOPIC,
                 THERAPY_EXPECTATION,
                 ADAPTIVE_COPING -> MemoryImportance.MEDIUM.score();

            case PERSON,
                 PREFERENCE -> MemoryImportance.LOW.score();
        };
    }
}