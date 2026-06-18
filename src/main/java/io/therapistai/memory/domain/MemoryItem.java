package io.therapistai.memory.domain;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public final class MemoryItem {

    private final UUID id;
    private final UUID userId;

    private final MemoryType type;
    private final MemoryKey key;
    private final String value;

    private final ConstraintType constraintType;
    private final MemoryStatus status;

    private final int version;
    private final UUID parentMemoryId;

    private final double confidence;
    private final int importance;

    private final UUID conversationId;
    private final UUID messageId;

    private final Map<String, Object> metadata;

    private final Instant createdAt;

    private MemoryItem(Builder builder) {
        this.id = builder.id;
        this.userId = builder.userId;
        this.type = builder.type;
        this.key = builder.key;
        this.value = builder.value;
        this.constraintType = builder.constraintType;
        this.status = builder.status;
        this.version = builder.version;
        this.parentMemoryId = builder.parentMemoryId;
        this.confidence = builder.confidence;
        this.importance = builder.importance;
        this.conversationId = builder.conversationId;
        this.messageId = builder.messageId;
        this.metadata = builder.metadata == null ? Map.of() : Map.copyOf(builder.metadata);
        this.createdAt = builder.createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public MemoryItem archived() {
        return builder()
                .id(id)
                .userId(userId)
                .type(type)
                .key(key)
                .value(value)
                .constraintType(constraintType)
                .status(MemoryStatus.ARCHIVED)
                .version(version)
                .parentMemoryId(parentMemoryId)
                .confidence(confidence)
                .importance(importance)
                .conversationId(conversationId)
                .messageId(messageId)
                .metadata(metadata)
                .createdAt(createdAt)
                .build();
    }

    public MemoryItem nextVersion(
            UUID newId,
            String newValue,
            double newConfidence,
            int newImportance,
            UUID newConversationId,
            UUID newMessageId,
            Map<String, Object> newMetadata,
            Instant now
    ) {
        return builder()
                .id(newId)
                .userId(userId)
                .type(type)
                .key(key)
                .value(newValue)
                .constraintType(constraintType)
                .status(MemoryStatus.ACTIVE)
                .version(version + 1)
                .parentMemoryId(id)
                .confidence(newConfidence)
                .importance(newImportance)
                .conversationId(newConversationId)
                .messageId(newMessageId)
                .metadata(newMetadata)
                .createdAt(now)
                .build();
    }

    public UUID id() {
        return id;
    }

    public UUID userId() {
        return userId;
    }

    public MemoryType type() {
        return type;
    }

    public MemoryKey key() {
        return key;
    }

    public String value() {
        return value;
    }

    public ConstraintType constraintType() {
        return constraintType;
    }

    public MemoryStatus status() {
        return status;
    }

    public int version() {
        return version;
    }

    public UUID parentMemoryId() {
        return parentMemoryId;
    }

    public double confidence() {
        return confidence;
    }

    public int importance() {
        return importance;
    }

    public UUID conversationId() {
        return conversationId;
    }

    public UUID messageId() {
        return messageId;
    }

    public Map<String, Object> metadata() {
        return metadata;
    }

    public String evidenceText() {
        Object value = metadata.get("evidenceText");
        return value instanceof String text ? text : null;
    }

    public Instant createdAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "MemoryItem{" +
                "id=" + id +
                ", userId=" + userId +
                ", type=" + type +
                ", key=" + key +
                ", constraintType=" + constraintType +
                ", status=" + status +
                ", version=" + version +
                ", parentMemoryId=" + parentMemoryId +
                ", confidence=" + confidence +
                ", importance=" + importance +
                ", conversationId=" + conversationId +
                ", messageId=" + messageId +
                ", metadata=" + metadata +
                ", createdAt=" + createdAt +
                '}';
    }

    public static final class Builder {

        private UUID id = UUID.randomUUID();
        private UUID userId;

        private MemoryType type;
        private MemoryKey key;
        private String value;

        private ConstraintType constraintType;
        private MemoryStatus status = MemoryStatus.ACTIVE;

        private int version = 1;
        private UUID parentMemoryId;

        private double confidence = 0.5;
        private int importance = MemoryImportance.MEDIUM.score();

        private UUID conversationId;
        private UUID messageId;

        private Map<String, Object> metadata = Map.of();

        private Instant createdAt = Instant.now();

        private Builder() {
        }

        public Builder id(UUID value) {
            this.id = value;
            return this;
        }

        public Builder userId(UUID value) {
            this.userId = value;
            return this;
        }

        public Builder type(MemoryType value) {
            this.type = value;

            if (value != null) {
                this.constraintType = value.constraintType();
            }

            return this;
        }

        public Builder key(MemoryKey value) {
            this.key = value;
            return this;
        }

        public Builder value(String value) {
            this.value = value;
            return this;
        }

        public Builder constraintType(ConstraintType value) {
            this.constraintType = value;
            return this;
        }

        public Builder status(MemoryStatus value) {
            this.status = value;
            return this;
        }

        public Builder version(int value) {
            this.version = value;
            return this;
        }

        public Builder parentMemoryId(UUID value) {
            this.parentMemoryId = value;
            return this;
        }

        public Builder confidence(double value) {
            this.confidence = Math.clamp(value, 0.0, 1.0);
            return this;
        }

        public Builder importance(int value) {
            this.importance = Math.clamp(value, 1, 10);
            return this;
        }

        public Builder conversationId(UUID value) {
            this.conversationId = value;
            return this;
        }

        public Builder messageId(UUID value) {
            this.messageId = value;
            return this;
        }

        public Builder metadata(Map<String, Object> value) {
            this.metadata = value == null ? Map.of() : Map.copyOf(value);
            return this;
        }

        public Builder createdAt(Instant value) {
            this.createdAt = value != null ? value : Instant.now();
            return this;
        }

        public MemoryItem build() {
            if (id == null) {
                throw new IllegalStateException("MemoryItem.id must not be null");
            }

            if (userId == null) {
                throw new IllegalStateException("MemoryItem.userId must not be null");
            }

            if (type == null) {
                throw new IllegalStateException("MemoryItem.type must not be null");
            }

            if (key == null) {
                throw new IllegalStateException("MemoryItem.key must not be null");
            }

            if (!type.allows(key)) {
                throw new IllegalStateException(
                        "Memory key '%s' is not allowed for memory type '%s'"
                                .formatted(key, type)
                );
            }

            if (value == null || value.isBlank()) {
                throw new IllegalStateException("MemoryItem.value must not be blank");
            }

            if (constraintType == null) {
                constraintType = type.constraintType();
            }

            if (constraintType != type.constraintType()) {
                throw new IllegalStateException(
                        "MemoryItem.constraintType must match MemoryType.constraintType"
                );
            }

            if (status == null) {
                throw new IllegalStateException("MemoryItem.status must not be null");
            }

            if (version < 1) {
                throw new IllegalStateException("MemoryItem.version must be >= 1");
            }

            if (type.isAccumulative() && parentMemoryId != null) {
                throw new IllegalStateException("ACCUMULATIVE memory must not have parentMemoryId");
            }

            if (type.isAccumulative() && version != 1) {
                throw new IllegalStateException("ACCUMULATIVE memory version must be 1");
            }

            if (conversationId == null) {
                throw new IllegalStateException("MemoryItem.conversationId must not be null");
            }

            if (messageId == null) {
                throw new IllegalStateException("MemoryItem.messageId must not be null");
            }

            if (metadata == null) {
                metadata = Map.of();
            }

            if (createdAt == null) {
                throw new IllegalStateException("MemoryItem.createdAt must not be null");
            }

            return new MemoryItem(this);
        }
    }
}