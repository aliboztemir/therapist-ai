-- ── V26 Message Analysis Persistence ─────────────────────────────────────────
-- Stores structured analysis output for each processed USER message.
-- SAFETY: Stores analysis metadata only. No raw message content.
-- message_id refers to chat_messages.id but no FK is used to keep modules decoupled.

CREATE TABLE IF NOT EXISTS message_analysis
(
    id
    UUID
    PRIMARY
    KEY,

    message_id
    UUID
    NOT
    NULL
    UNIQUE,

    user_id
    UUID
    NOT
    NULL,
    conversation_id
    UUID
    NOT
    NULL,

    message_type
    VARCHAR
(
    50
) NOT NULL,
    user_intent VARCHAR
(
    50
) NOT NULL,

    primary_emotion VARCHAR
(
    50
) NOT NULL,
    secondary_emotion VARCHAR
(
    50
) NOT NULL,

    sentiment VARCHAR
(
    50
) NOT NULL,
    emotional_intensity SMALLINT NOT NULL,

    temporal_focus VARCHAR
(
    50
) NOT NULL,
    disclosure_level VARCHAR
(
    50
) NOT NULL,

    advice_seeking BOOLEAN NOT NULL DEFAULT FALSE,
    boundary_test BOOLEAN NOT NULL DEFAULT FALSE,
    crisis_signal_detected BOOLEAN NOT NULL DEFAULT FALSE,

    confidence DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    fallback_reason VARCHAR
(
    255
),

    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_message_analysis_emotional_intensity
    CHECK
(
    emotional_intensity
    BETWEEN
    0
    AND
    10
),
    CONSTRAINT chk_message_analysis_confidence
    CHECK
(
    confidence
    >=
    0.0
    AND
    confidence
    <=
    1.0
)
    );

CREATE INDEX IF NOT EXISTS idx_message_analysis_message_id
    ON message_analysis (message_id);

CREATE INDEX IF NOT EXISTS idx_message_analysis_user_id
    ON message_analysis (user_id);

CREATE INDEX IF NOT EXISTS idx_message_analysis_conversation_id
    ON message_analysis (conversation_id);

CREATE INDEX IF NOT EXISTS idx_message_analysis_created_at
    ON message_analysis (created_at DESC);

-- ── Themes ──────────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS message_analysis_themes
(
    message_analysis_id
    UUID
    NOT
    NULL,
    theme
    VARCHAR
(
    50
) NOT NULL,
    PRIMARY KEY
(
    message_analysis_id,
    theme
),
    CONSTRAINT fk_message_analysis_themes_analysis
    FOREIGN KEY
(
    message_analysis_id
)
    REFERENCES message_analysis
(
    id
)
    ON DELETE CASCADE
    );

CREATE INDEX IF NOT EXISTS idx_message_analysis_themes_analysis_id
    ON message_analysis_themes (message_analysis_id);

-- ── Communication Styles ────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS message_analysis_communication_styles
(
    message_analysis_id
    UUID
    NOT
    NULL,
    communication_style
    VARCHAR
(
    50
) NOT NULL,
    PRIMARY KEY
(
    message_analysis_id,
    communication_style
),
    CONSTRAINT fk_message_analysis_styles_analysis
    FOREIGN KEY
(
    message_analysis_id
)
    REFERENCES message_analysis
(
    id
)
    ON DELETE CASCADE
    );

CREATE INDEX IF NOT EXISTS idx_message_analysis_styles_analysis_id
    ON message_analysis_communication_styles (message_analysis_id);

-- ── Cognitive Signals ───────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS message_analysis_cognitive_signals
(
    message_analysis_id
    UUID
    NOT
    NULL,
    cognitive_signal
    VARCHAR
(
    50
) NOT NULL,
    PRIMARY KEY
(
    message_analysis_id,
    cognitive_signal
),
    CONSTRAINT fk_message_analysis_cognitive_signals_analysis
    FOREIGN KEY
(
    message_analysis_id
)
    REFERENCES message_analysis
(
    id
)
    ON DELETE CASCADE
    );

CREATE INDEX IF NOT EXISTS idx_message_analysis_cognitive_signals_analysis_id
    ON message_analysis_cognitive_signals (message_analysis_id);