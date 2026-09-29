-- 일반 시험지는 제한시간 없이 경과 시간만 기록한다.
-- LIMITED 시험지 attempt에는 기존처럼 expires_at 값이 저장된다.
ALTER TABLE assessment_attempt
	MODIFY COLUMN expires_at DATETIME(6) NULL;
