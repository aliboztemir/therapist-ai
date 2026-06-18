package io.therapistai.memory.application;

import io.therapistai.memory.domain.MemorySnapshot;

public interface MemoryService {

    MemorySnapshot retrieve(MemoryRetrievalQuery query);

    void extractAndPersist(MemoryExtractionCommand command);
}