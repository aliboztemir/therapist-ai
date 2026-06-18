CREATE
EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE app_users
(
    id                   BIGSERIAL PRIMARY KEY,
    user_uuid            UUID         NOT NULL DEFAULT gen_random_uuid(),

    username             VARCHAR(255) NOT NULL,
    email                VARCHAR(254) NOT NULL,
    password             VARCHAR(255) NOT NULL,

    full_name            VARCHAR(100) NOT NULL,
    preferred_name       VARCHAR(100),
    birth_date           DATE,
    gender               VARCHAR(30),
    country              VARCHAR(100),
    city                 VARCHAR(100),
    preferred_language   VARCHAR(10),
    timezone             VARCHAR(80),

    enabled              BOOLEAN      NOT NULL DEFAULT TRUE,
    onboarding_completed BOOLEAN      NOT NULL DEFAULT FALSE,

    created_at           TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMP,

    CONSTRAINT uq_app_users_user_uuid UNIQUE (user_uuid),
    CONSTRAINT uq_app_users_username UNIQUE (username),
    CONSTRAINT uq_app_users_email UNIQUE (email)
);

CREATE INDEX idx_app_users_user_uuid ON app_users (user_uuid);
CREATE INDEX idx_app_users_username ON app_users (username);
CREATE INDEX idx_app_users_email ON app_users (email);