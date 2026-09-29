# 무제한 시험지 attempt 배포 절차

1. 운영 DB 백업 또는 스냅샷을 확인한다.
2. `V20260929_01__unlimited_assessment_attempt.sql`을 적용한다.
3. `assessment_attempt.expires_at`이 nullable인지 확인한다.
4. 백엔드 배포 후 일반 시험지를 시작해 `answer_unlocked_at = started_at`, `expires_at IS NULL`인지 확인한다.
5. 제한 시험지는 기존처럼 `expires_at`이 채워지는지 회귀 확인한다.

이 변경은 기존 행을 삭제하거나 수정하지 않는다.
