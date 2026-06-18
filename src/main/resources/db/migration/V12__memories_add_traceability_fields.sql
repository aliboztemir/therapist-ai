CREATE TABLE IF NOT EXISTS memories
(
    id
    UUID
    PRIMARY
    KEY,

    user_id
    UUID
    NOT
    NULL,

    memory_type
    VARCHAR
(
    100
) NOT NULL,
    memory_key VARCHAR
(
    100
) NOT NULL,
    memory_value TEXT NOT NULL,

    constraint_type VARCHAR
(
    30
) NOT NULL,
    status VARCHAR
(
    30
) NOT NULL,

    version INTEGER NOT NULL DEFAULT 1,
    parent_memory_id UUID,

    confidence DOUBLE PRECISION NOT NULL,
    importance INTEGER NOT NULL,

    conversation_id UUID NOT NULL,
    message_id UUID NOT NULL,

    metadata JSONB NOT NULL DEFAULT '{}'::jsonb,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW
(
),
    CONSTRAINT chk_memories_confidence
    CHECK
(
    confidence
    >=
    0.0
    AND
    confidence
    <=
    1.0
),
    CONSTRAINT chk_memories_importance
    CHECK
(
    importance
    >=
    1
    AND
    importance
    <=
    10
),
    CONSTRAINT chk_memories_version
    CHECK
(
    version
    >=
    1
)
    );

CREATE INDEX IF NOT EXISTS idx_memories_user_id
    ON memories (user_id);

CREATE INDEX IF NOT EXISTS idx_memories_type
    ON memories (memory_type);

CREATE INDEX IF NOT EXISTS idx_memories_key
    ON memories (memory_key);

CREATE INDEX IF NOT EXISTS idx_memories_user_type
    ON memories (user_id, memory_type);

CREATE INDEX IF NOT EXISTS idx_memories_user_type_key_status
    ON memories (user_id, memory_type, memory_key, status);

CREATE INDEX IF NOT EXISTS idx_memories_conversation_id
    ON memories (conversation_id);

CREATE INDEX IF NOT EXISTS idx_memories_message_id
    ON memories (message_id);

CREATE INDEX IF NOT EXISTS idx_memories_status
    ON memories (status);

CREATE INDEX IF NOT EXISTS idx_memories_parent_memory_id
    ON memories (parent_memory_id);

CREATE INDEX IF NOT EXISTS idx_memories_created_at
    ON memories (created_at);