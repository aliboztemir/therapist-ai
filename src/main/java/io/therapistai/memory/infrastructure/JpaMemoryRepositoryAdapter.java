package io.therapistai.memory.infrastructure;

import io.therapistai.memory.domain.MemoryItem;
import io.therapistai.memory.domain.MemoryKey;
import io.therapistai.memory.domain.MemoryRepository;
import io.therapistai.memory.domain.MemoryStatus;
import io.therapistai.memory.domain.MemoryType;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaMemoryRepositoryAdapter implements MemoryRepository {

    private final SpringDataMemoryRepository springRepo;

    public JpaMemoryRepositoryAdapter(SpringDataMemoryRepository springRepo) {
        this.springRepo = springRepo;
    }

    @Override
    public List<MemoryItem> findByUserId(UUID userId) {
        if (userId == null) {
            return List.of();
        }

        return springRepo.findByUserIdAndStatusOrderByCreatedAtDesc(
                        userId,
                        MemoryStatus.ACTIVE
                )
                .stream()
                .map(MemoryMapper::toDomain)
                .toList();
    }


    @Override
    public Optional<MemoryItem> findActiveByUserIdAndTypeAndKey(
            UUID userId,
            MemoryType type,
            MemoryKey key
    ) {
        if (userId == null || type == null || key == null) {
            return Optional.empty();
        }

        return springRepo
                .findFirstByUserIdAndMemoryTypeAndMemoryKeyAndStatusOrderByCreatedAtDesc(
                        userId,
                        type,
                        key,
                        MemoryStatus.ACTIVE
                )
                .map(MemoryMapper::toDomain);
    }

    @Override
    public MemoryItem save(MemoryItem item) {
        MemoryEntity entity = MemoryMapper.toEntity(item);
        MemoryEntity saved = springRepo.save(entity);
        return MemoryMapper.toDomain(saved);
    }
}