ALTER TABLE assessment
	ADD COLUMN answer_input_delay_seconds BIGINT NOT NULL DEFAULT 0;

CREATE TABLE assessment_attempt (
	id BIGINT NOT NULL AUTO_INCREMENT,
	assessment_id BIGINT NOT NULL,
	member_id BIGINT NOT NULL,
	attempt_number INT NOT NULL,
	started_at DATETIME(6) NOT NULL,
	answer_unlocked_at DATETIME(6) NOT NULL,
	expires_at DATETIME(6) NOT NULL,
	status VARCHAR(32) NOT NULL,
	submission_id BIGINT NULL,
	active_key VARCHAR(255) NULL,
	created_at DATETIME(6) NOT NULL,
	updated_at DATETIME(6) NOT NULL,
	PRIMARY KEY (id),
	CONSTRAINT uk_assessment_attempt_number
		UNIQUE (assessment_id, member_id, attempt_number),
	CONSTRAINT uk_assessment_attempt_active_key
		UNIQUE (active_key),
	INDEX idx_assessment_attempt_assessment_member (assessment_id, member_id),
	INDEX idx_assessment_attempt_submission (submission_id)
);
