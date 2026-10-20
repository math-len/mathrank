package kr.co.mathrank.domain.problem.assessment.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentAttempt;

public interface AssessmentAttemptRepository extends JpaRepository<AssessmentAttempt, Long> {
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT attempt FROM AssessmentAttempt attempt WHERE attempt.activeKey = :activeKey")
	Optional<AssessmentAttempt> findByActiveKeyForUpdate(@Param("activeKey") String activeKey);

	Optional<AssessmentAttempt> findByActiveKey(String activeKey);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT attempt FROM AssessmentAttempt attempt WHERE attempt.id = :attemptId")
	Optional<AssessmentAttempt> findByIdForUpdate(@Param("attemptId") Long attemptId);

	@Query("""
		SELECT attempt.id FROM AssessmentAttempt attempt
		WHERE attempt.status = kr.co.mathrank.domain.problem.assessment.entity.AssessmentAttemptStatus.ACTIVE
			AND attempt.expiresAt IS NULL
			AND attempt.startedAt <= :threshold
		ORDER BY attempt.startedAt
		""")
	List<Long> findAutoSubmitDueIds(@Param("threshold") Instant threshold, Pageable pageable);

	Optional<AssessmentAttempt> findTopByAssessmentIdAndMemberIdOrderByAttemptNumberDesc(
		Long assessmentId,
		Long memberId
	);
}
