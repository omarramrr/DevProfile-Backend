CREATE TABLE analysis_results (
    id                    BIGSERIAL PRIMARY KEY,
    session_id            BIGINT NOT NULL,
    overall_score         INTEGER NOT NULL,
    code_quality_score    INTEGER NOT NULL,
    complexity_score      INTEGER NOT NULL,
    activity_score        INTEGER NOT NULL,
    resume_score          INTEGER NOT NULL,
    tech_align_score      INTEGER NOT NULL,
    recruiter_perspective TEXT NOT NULL,
    strengths             TEXT NOT NULL,
    weaknesses            TEXT NOT NULL,
    percentile_ranking    VARCHAR(50),
    created_at            TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_analysis_result_session
        FOREIGN KEY (session_id) REFERENCES analysis_sessions(id) ON DELETE CASCADE,
    CONSTRAINT uq_analysis_result_session UNIQUE (session_id)
);