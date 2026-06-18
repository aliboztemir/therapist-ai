package io.therapistai.memory.infrastructure;

import io.therapistai.memory.domain.ConstraintType;
import io.therapistai.memory.domain.MemoryKey;
import io.therapistai.memory.domain.MemoryStatus;
import io.therapistai.memory.domain.MemoryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "memories",
        indexes = {
                @Index(name = "idx_memories_user_id", columnList = "user_id"),
                @Index(name = "idx_memories_type", columnList = "memory_type"),
                @Index(name = "idx_memories_key", columnList = "memory_key"),
                @Index(name = "idx_memories_user_type", columnList = "user_id,memory_type"),
                @Index(name = "idx_memories_user_type_key_status", columnList = "user_id,memory_type,memory_key,status"),
                @Index(name = "idx_memories_conversation_id", columnList = "conversation_id"),
                @Index(name = "idx_memories_message_id", columnList = "message_id"),
                @Index(name = "idx_memories_status", columnList = "status"),
                @Index(name = "idx_memories_parent_memory_id", columnList = "parent_memory_id"),
                @Index(name = "idx_memories_created_at", columnList = "created_at")
        }
)
public class MemoryEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "memory_type", nullable = false, length = 100)
    private MemoryType memoryType;

    @Enumerated(EnumType.STRING)
    @Column(name = "memory_key", nullable = false, length = 100)
    private MemoryKey memoryKey;

    @Column(name = "memory_value", nullable = false, columnDefinition = "TEXT")
    private String memoryValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "constraint_type", nullable = false, length = 30)
    private ConstraintType constraintType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private MemoryStatus status;

    @Column(name = "version", nullable = false)
    private Integer version;

    @Column(name = "parent_memory_id")
    private UUID parentMemoryId;

    @Column(name = "confidence", nullable = false)
    private Double confidence;

    @Column(name = "importance", nullable = false)
    private Integer importance;

    @Column(name = "conversation_id", nullable = false)
    private UUID conversationId;

    @Column(name = "message_id", nullable = false)
    private UUID messageId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> metadata = Map.of();

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public MemoryEntity(
            UUID id,
            UUID userId,
            MemoryType memoryType,
            MemoryKey memoryKey,
            String memoryValue,
            ConstraintType constraintType,
            MemoryStatus status,
            Integer version,
            UUID parentMemoryId,
            Double confidence,
            Integer importance,
            UUID conversationId,
            UUID messageId,
            Map<String, Object> metadata,
            Instant createdAt
    ) {
        this.id = id;
        this.userId = userId;
        this.memoryType = memoryType;
        this.memoryKey = memoryKey;
        this.memoryValue = memoryValue;
        this.constraintType = constraintType;
        this.status = status;
        this.version = version;
        this.parentMemoryId = parentMemoryId;
        this.confidence = confidence;
        this.importance = importance;
        this.conversationId = conversationId;
        this.messageId = messageId;
        this.metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
        this.createdAt = createdAt;
    }

    public Map<String, Object> getMetadata() {
        return metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }

    public void archive() {
        this.status = MemoryStatus.ARCHIVED;
    }
}