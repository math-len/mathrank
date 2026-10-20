-- 진행 중 응시의 임시 저장 답안. 시작 3시간 뒤 이 답안으로 자동 제출한다.
-- 기존 행은 NULL로 남으며 삭제·수정하지 않는다.
ALTER TABLE assessment_attempt
	ADD COLUMN draft_answers JSON NULL,
	ADD COLUMN draft_saved_at DATETIME(6) NULL;
