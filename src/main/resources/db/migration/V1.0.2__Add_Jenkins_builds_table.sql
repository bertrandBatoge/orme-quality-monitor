CREATE SEQUENCE IF NOT EXISTS jenkins_builds_SEQ START WITH 1 INCREMENT BY 50;

CREATE TABLE IF NOT EXISTS jenkins_builds (
    id BIGINT PRIMARY KEY DEFAULT nextval('jenkins_builds_SEQ'),
    jenkins_job_id BIGINT REFERENCES jenkins_jobs(id),
    build_number INTEGER,
    result VARCHAR(255),
    timestamp TIMESTAMP,
    duration BIGINT,
    url VARCHAR(1024)
);

CREATE INDEX IF NOT EXISTS idx_jenkins_builds_job_id ON jenkins_builds(jenkins_job_id);
