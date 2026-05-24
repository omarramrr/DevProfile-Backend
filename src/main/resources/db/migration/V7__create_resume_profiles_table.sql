CREATE TABLE resume_profiles (
    id                BIGSERIAL PRIMARY KEY,
    original_filename VARCHAR(255) NOT NULL,
    file_size         BIGINT NOT NULL,
    file_path         VARCHAR(500) NOT NULL,
    extracted_text    TEXT NOT NULL,
    session_id        BIGINT NOT NULL,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_resume_profile_session
        FOREIGN KEY (session_id) REFERENCES analysis_sessions(id) ON DELETE CASCADE,
    CONSTRAINT uq_resume_profile_session UNIQUE (session_id)
);