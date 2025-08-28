package kr.co.mathrank.domain.problem.assessment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentSubmission;

public interface AssessmentSubmissionRepository extends JpaRepository<AssessmentSubmission, Long> {
	@Query("""
		SELECT assessmentSubmission FROM AssessmentSubmission assessmentSubmission 
				LEFT JOIN FETCH assessmentSubmission.assessment
				LEFT JOIN FETCH assessmentSubmission.submittedItemAnswers sia 
						LEFT JOIN FETCH sia.assessmentItem 
								WHERE assessmentSubmission.id = :assessmentSubmissionId
		""")
	Optional<AssessmentSubmission> findByAssessmentSubmissionId(@Param("assessmentSubmissionId") Long assessmentSubmissionId);

	@Query("""
		SELECT s FROM AssessmentSubmission s
		LEFT JOIN FETCH s.submittedItemAnswers sItems
		LEFT JOIN FETCH sItems.assessmentItem
		WHERE s.id = :assessmentSubmissionId
		""")
	Optional<AssessmentSubmission> findByIdWithSubmittedItemAnswers(@Param("assessmentSubmissionId") Long assessmentSubmissionId);

	@Query("""
		SELECT ass
		FROM AssessmentSubmission ass
		WHERE ass.assessment.id = :assessmentId AND ass.memberId = :memberId
		""")
	@Lock(LockModeType.PESSIMISTIC_READ)
	List<AssessmentSubmission> findAllByAssessmentIdAndMemberIdForShare(@Param("assessmentId") Long assessmentId, @Param("memberId") Long memberId);
}
