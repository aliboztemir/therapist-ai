-- flyway:noTransaction
-- ──────────────────────────────────────────────────────────────────────────
-- V1  Initial schema
--
-- Requires: pgvector/pgvector:pg16 (or any PostgreSQL with vector extension)
-- Embedding dimension: 1536 (OpenAI text-embedding-3-small / ada-002 default)
-- Change vector(1536) to vector(N) for other models before first deployment.
-- ──────────────────────────────────────────────────────────────────────────

-- pgvector extension
CREATE
EXTENSION IF NOT EXISTS vector;

-- ── documents ──────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS documents
(
    id
    UUID
    NOT
    NULL,
    title
    TEXT
    NOT
    NULL,
    content
    TEXT
    NOT
    NULL,
    source
    TEXT,
    created_at
    TIMESTAMP
    NOT
    NULL,
    CONSTRAINT
    pk_documents
    PRIMARY
    KEY
(
    id
)
    );

-- ── document_chunks ────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS document_chunks
(
    id
    UUID
    NOT
    NULL,
    document_id
    UUID
    NOT
    NULL,
    chunk_index
    INT
    NOT
    NULL,
    content
    TEXT
    NOT
    NULL,
    start_offset
    INT
    NOT
    NULL,
    end_offset
    INT
    NOT
    NULL,
    CONSTRAINT
    pk_document_chunks
    PRIMARY
    KEY
(
    id
),
    CONSTRAINT fk_chunks_document
    FOREIGN KEY
(
    document_id
) REFERENCES documents
(
    id
) ON DELETE CASCADE
    );

CREATE INDEX IF NOT EXISTS idx_document_chunks_document_id
    ON document_chunks (document_id);

-- ── document_embeddings ────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS document_embeddings
(
    id
    UUID
    NOT
    NULL,
    document_id
    UUID
    NOT
    NULL,
    chunk_id
    UUID
    NOT
    NULL,
    chunk_index
    INT
    NOT
    NULL,
    content
    TEXT
    NOT
    NULL,
    embedding
    vector
(
    1536
) NOT NULL,
    CONSTRAINT pk_document_embeddings PRIMARY KEY
(
    id
),
    CONSTRAINT fk_embeddings_chunk
    FOREIGN KEY
(
    chunk_id
) REFERENCES document_chunks
(
    id
) ON DELETE CASCADE
    );

-- HNSW index for cosine similarity search (Phase 7 — RAG)
-- HNSW builds on empty tables; IVFFlat requires existing rows.
CREATE INDEX IF NOT EXISTS idx_document_embeddings_hnsw
    ON document_embeddings USING hnsw (embedding vector_cosine_ops);
