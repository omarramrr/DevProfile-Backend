CREATE TABLE github_profiles (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255),
    total_repos INTEGER,
    total_stars INTEGER,
    contributions_last_year INTEGER,
    session_id BIGINT NOT NULL,
    user_id BIGINT,
    CONSTRAINT fk_github_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_github_profile_session
        FOREIGN KEY (session_id) REFERENCES analysis_sessions(id) ON DELETE CASCADE,
    CONSTRAINT uq_github_profile_session UNIQUE (session_id)
);

CREATE TABLE github_repository_snapshots (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    description TEXT,
    primary_language VARCHAR(100),
    stars INTEGER,
    last_updated TIMESTAMPTZ,
    github_profile_id BIGINT NOT NULL,
    CONSTRAINT fk_snapshot_github_profile
        FOREIGN KEY (github_profile_id) REFERENCES github_profiles(id) ON DELETE CASCADE
);

