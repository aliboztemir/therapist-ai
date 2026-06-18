-- ── chat_messages ──────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS chat_messages
(
    id
    UUID
    NOT
    NULL,
    conversation_id
    UUID
    NOT
    NULL,
    user_id
    UUID
    NOT
    NULL,
    role
    VARCHAR
(
    20
) NOT NULL,
    content TEXT NOT NULL,
    message_order INT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_chat_messages PRIMARY KEY
(
    id
),
    CONSTRAINT fk_chat_messages_conversation
    FOREIGN KEY
(
    conversation_id
)
    REFERENCES conversations
(
    id
)
                         ON DELETE CASCADE,
    CONSTRAINT uq_chat_messages_conversation_order
    UNIQUE
(
    conversation_id,
    message_order
),
    CONSTRAINT chk_chat_messages_role
    CHECK
(
    role
    IN
(
    'USER',
    'ASSISTANT'
))
    );

CREATE INDEX IF NOT EXISTS idx_chat_messages_conversation_id
    ON chat_messages (conversation_id);

CREATE INDEX IF NOT EXISTS idx_chat_messages_user_id
    ON chat_messages (user_id);

CREATE INDEX IF NOT EXISTS idx_chat_messages_conversation_order
    ON chat_messages (conversation_id, message_order);

CREATE INDEX IF NOT EXISTS idx_chat_messages_conversation_created_at
    ON chat_messages (conversation_id, created_at);