-- ── conversations ─────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS conversations
(
    id
    UUID
    NOT
    NULL,
    user_id
    UUID
    NOT
    NULL,
    created_at
    TIMESTAMP
    WITH
    TIME
    ZONE
    NOT
    NULL,
    updated_at
    TIMESTAMP
    WITH
    TIME
    ZONE
    NOT
    NULL,

    CONSTRAINT
    pk_conversations
    PRIMARY
    KEY
(
    id
)
    );

CREATE INDEX IF NOT EXISTS idx_conversations_user_id
    ON conversations (user_id);

CREATE INDEX IF NOT EXISTS idx_conversations_updated_at
    ON conversations (updated_at);