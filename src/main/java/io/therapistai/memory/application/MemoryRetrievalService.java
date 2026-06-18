package io.therapistai.memory.application;

import io.therapistai.memory.domain.MemoryCandidate;
import io.therapistai.memory.domain.MemoryItem;
import io.therapistai.memory.domain.MemoryRepository;
import io.therapistai.memory.domain.MemorySnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Service
public class MemoryRetrievalService implements MemoryService {

    private static final Logger log =
            LoggerFactory.getLogger(MemoryRetrievalService.class);

    private final MemoryRepository memoryRepository;
    private final MemoryRankingService rankingService;
    private final MemoryExtractionService extractionService;
    private final MemoryPersistenceService persistenceService;

    public MemoryRetrievalService(
            MemoryRepository memoryRepository,
            MemoryRankingService rankingService,
            MemoryExtractionService extractionService,
            MemoryPersistenceService persistenceService
    ) {
        this.memoryRepository = memoryRepository;
        this.rankingService = rankingService;
        this.extractionService = extractionService;
        this.persistenceService = persistenceService;
    }

    @Override
    public MemorySnapshot retrieve(MemoryRetrievalQuery query) {
        Objects.requireNonNull(query, "query must not be null");

        try {
            List<MemoryItem> memories = memoryRepository.findByUserId(query.userId());

            if (memories == null || memories.isEmpty()) {
                return MemorySnapshot.empty();
            }

            MemorySignalContext signalContext =
                    query.signalContext() != null
                            ? query.signalContext()
                            : MemorySignalContext.empty();

            List<MemoryItem> ranked =
                    rankingService.rank(
                            memories,
                            query.currentMessage(),
                            signalContext,
                            Instant.now()
                    );

            if (ranked.isEmpty()) {
                return MemorySnapshot.empty();
            }

            List<MemoryItem> selected =
                    ranked.stream()
                            .limit(query.limit())
                            .toList();

            log.debug(
                    "memory.retrieval.selected userId={} conversationId={} selected={} limit={}",
                    query.userId(),
                    query.conversationId(),
                    selected.size(),
                    query.limit()
            );

            return MemorySnapshot.fromList(selected);

        } catch (Exception ex) {
            log.warn(
                    "memory.retrieval.failed userId={} conversationId={} error={}",
                    query.userId(),
                    query.conversationId(),
                    ex.getMessage()
            );
            return MemorySnapshot.empty();
        }
    }

    @Override
    public void extractAndPersist(
            MemoryExtractionCommand command
    ) {
        Objects.requireNonNull(command, "command must not be null");

        List<MemoryCandidate> candidates =
                extractionService.extract(
                        command.currentMessage(),
                        command.signalContext()
                );

        persistenceService.persist(
                command.userId(),
                command.conversationId(),
                command.messageId(),
                candidates
        );
    }
}