package kr.co.mathrank.domain.problem.assessment.repository;

import java.util.Optional;

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

	Optional<AssessmentAttempt> findTopByAssessmentIdAndMemberIdOrderByAttemptNumberDesc(
		Long assessmentId,
		Long memberId
	);
}
