CREATE TABLE roadmaps (
    id          BIGSERIAL PRIMARY KEY,
    session_id  BIGINT NOT NULL,
    summary     TEXT,
    total_weeks INTEGER NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_roadmap_session
        FOREIGN KEY (session_id) REFERENCES analysis_sessions(id) ON DELETE CASCADE,
    CONSTRAINT uq_roadmap_session UNIQUE (session_id)
);

CREATE TABLE roadmap_weeks (
    id                  BIGSERIAL PRIMARY KEY,
    roadmap_id          BIGINT NOT NULL,
    week_number         INTEGER NOT NULL,
    theme               VARCHAR(255) NOT NULL,
    technical_tasks     TEXT NOT NULL,
    measurable_outcomes TEXT NOT NULL,
    technologies        TEXT NOT NULL,
    project_idea        TEXT,
    CONSTRAINT fk_roadmap_week_roadmap
        FOREIGN KEY (roadmap_id) REFERENCES roadmaps(id) ON DELETE CASCADE
);