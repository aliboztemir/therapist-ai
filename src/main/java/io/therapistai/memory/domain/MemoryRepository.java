package io.therapistai.memory.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MemoryRepository {

    List<MemoryItem> findByUserId(UUID userId);

    Optional<MemoryItem> findActiveByUserIdAndTypeAndKey(
            UUID userId,
            MemoryType type,
            MemoryKey key
    );

    MemoryItem save(MemoryItem item);
}