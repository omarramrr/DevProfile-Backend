ALTER TABLE analysis_sessions ADD COLUMN user_id BIGINT;
ALTER TABLE analysis_sessions
ADD CONSTRAINT fk_sessions_user
FOREIGN KEY (user_id) REFERENCES users(id);
ALTER TABLE analysis_sessions ADD COLUMN report_viewed_at TIMESTAMPTZ;
ALTER TABLE analysis_sessions ADD COLUMN name VARCHAR(100);
UPDATE analysis_sessions SET name = 'Session #' || id WHERE name IS NULL;
ALTER TABLE analysis_sessions ALTER COLUMN name SET NOT NULL;
ALTER TABLE users ADD COLUMN tech_field VARCHAR(30);
ALTER TABLE users ADD COLUMN career_goal VARCHAR(30);
ALTER TABLE analysis_sessions ADD COLUMN tech_field VARCHAR(30);
ALTER TABLE analysis_sessions ADD COLUMN career_goal VARCHAR(30);