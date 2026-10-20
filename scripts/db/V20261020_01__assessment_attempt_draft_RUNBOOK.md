# 응시 답안 임시 저장 · 3시간 자동 제출 배포 절차

1. 운영 RDS 수동 스냅샷을 만든다 (자동 백업 보존 0일). 이름 예: `database-2-pre-attempt-draft-20261020`.
2. 사전 확인: `SHOW COLUMNS FROM assessment_attempt LIKE 'draft%';` 결과가 비어 있어야 한다.
3. `V20261020_01__assessment_attempt_draft.sql`을 적용한다.
4. 사후 확인: 같은 쿼리로 `draft_answers`(json, NULL), `draft_saved_at`(datetime(6), NULL) 두 컬럼을 확인한다.
5. 백엔드 배포 전 현재 미제출 응시 규모 확인 (배포 후 5분 안에 자동 제출될 대상):
   `SELECT COUNT(*) FROM assessment_attempt WHERE status = 'ACTIVE' AND expires_at IS NULL AND started_at <= UTC_TIMESTAMP(6) - INTERVAL 3 HOUR;`
   이 응시들은 저장 답안이 없으므로 빈 답안(0점)으로 제출된다. 단, 이미 제출 기록이 있는 회원의 응시는 제출하지 않고 만료 처리된다.
6. 백엔드 배포 후 확인:
   - Swagger에 `PUT /api/v1/problem/assessment/attempts/{attemptId}/draft` 노출
   - 응시 시작 응답에 `autoSubmitAt`, `draftAnswers` 포함
   - 5번 쿼리 결과가 0이 되는지 (최대 5분 + 처리 시간)

롤백: 애플리케이션을 이전 태그로 되돌린다. 컬럼은 NULL 허용이므로 남겨 두어도 이전 버전과 호환된다.
필요 시 `ALTER TABLE assessment_attempt DROP COLUMN draft_answers, DROP COLUMN draft_saved_at;`
