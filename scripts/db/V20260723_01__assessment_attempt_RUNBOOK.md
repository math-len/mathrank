# Assessment attempt 운영 DB 반영

이 저장소는 운영 Flyway/Liquibase 정본을 사용하지 않으므로 애플리케이션 배포 전에
`V20260723_01__assessment_attempt.sql`을 운영 MySQL에 한 번만 수동 적용한다.
`spring.jpa.hibernate.ddl-auto=update`는 사용하지 않는다.

## 사전 확인

```sql
SELECT VERSION();

SELECT COUNT(*) AS assessment_table_count
FROM information_schema.tables
WHERE table_schema = DATABASE()
  AND table_name = 'assessment';

SELECT COUNT(*) AS delay_column_count
FROM information_schema.columns
WHERE table_schema = DATABASE()
  AND table_name = 'assessment'
  AND column_name = 'answer_input_delay_seconds';

SELECT COUNT(*) AS attempt_table_count
FROM information_schema.tables
WHERE table_schema = DATABASE()
  AND table_name = 'assessment_attempt';
```

`assessment_table_count=1`, `delay_column_count=0`, `attempt_table_count=0`일 때만 migration을 실행한다.

## 적용 순서

1. 운영 DB 백업 또는 복구 지점을 확인한다.
2. `V20260723_01__assessment_attempt.sql`을 실행한다.
3. 아래 사후 검증을 실행한다.
4. 검증 성공 후 신규 애플리케이션을 배포한다.

## 사후 검증

```sql
SELECT column_name, column_type, is_nullable, column_default
FROM information_schema.columns
WHERE table_schema = DATABASE()
  AND table_name = 'assessment'
  AND column_name = 'answer_input_delay_seconds';

SHOW CREATE TABLE assessment_attempt;

SELECT COUNT(*) AS non_zero_existing_delay_count
FROM assessment
WHERE answer_input_delay_seconds <> 0;
```

기존 assessment의 delay는 모두 `0`이어야 한다.

## 롤백

신규 애플리케이션 배포에 실패하면 이전 애플리케이션 버전으로 되돌린다. 추가된 컬럼과
테이블은 이전 버전이 참조하지 않으므로 즉시 삭제하지 않는다. 데이터 삭제가 필요한
down migration은 별도 점검과 백업 후 수행한다.
