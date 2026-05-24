CREATE TABLE analysis_sessions (
    id BIGSERIAL PRIMARY KEY,
    status VARCHAR(40) NOT NULL,
    workflow_step VARCHAR(40),
    hireability_score INTEGER,
    archived BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);