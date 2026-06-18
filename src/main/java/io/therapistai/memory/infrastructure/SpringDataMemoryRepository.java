package io.therapistai.memory.infrastructure;

import io.therapistai.memory.domain.MemoryKey;
import io.therapistai.memory.domain.MemoryStatus;
import io.therapistai.memory.domain.MemoryType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

interface SpringDataMemoryRepository extends JpaRepository<MemoryEntity, UUID> {

    List<MemoryEntity> findByUserIdAndStatusOrderByCreatedAtDesc(
            UUID userId,
            MemoryStatus status
    );

    List<MemoryEntity> findByUserIdAndMemoryTypeInAndStatusOrderByCreatedAtDesc(
            UUID userId,
            Set<MemoryType> memoryTypes,
            MemoryStatus status
    );

    Optional<MemoryEntity> findFirstByUserIdAndMemoryTypeAndMemoryKeyAndStatusOrderByCreatedAtDesc(
            UUID userId,
            MemoryType memoryType,
            MemoryKey memoryKey,
            MemoryStatus status
    );

    List<MemoryEntity> findByUserIdAndMemoryTypeAndMemoryKeyOrderByVersionAscCreatedAtAsc(
            UUID userId,
            MemoryType memoryType,
            MemoryKey memoryKey
    );
}