package io.therapistai.memory.infrastructure;

import io.therapistai.memory.domain.MemoryItem;

import java.time.Instant;
import java.util.UUID;

final class MemoryMapper {

    private MemoryMapper() {
    }

    static MemoryItem toDomain(MemoryEntity entity) {
        return MemoryItem.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .type(entity.getMemoryType())
                .key(entity.getMemoryKey())
                .value(entity.getMemoryValue())
                .constraintType(entity.getConstraintType())
                .status(entity.getStatus())
                .version(entity.getVersion())
                .parentMemoryId(entity.getParentMemoryId())
                .confidence(entity.getConfidence())
                .importance(entity.getImportance())
                .conversationId(entity.getConversationId())
                .messageId(entity.getMessageId())
                .metadata(entity.getMetadata())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    static MemoryEntity toEntity(MemoryItem item) {
        UUID id = item.id() != null
                ? item.id()
                : UUID.randomUUID();

        return new MemoryEntity(
                id,
                item.userId(),
                item.type(),
                item.key(),
                item.value(),
                item.constraintType(),
                item.status(),
                item.version(),
                item.parentMemoryId(),
                item.confidence(),
                item.importance(),
                item.conversationId(),
                item.messageId(),
                item.metadata(),
                item.createdAt() != null ? item.createdAt() : Instant.now()
        );
    }
}