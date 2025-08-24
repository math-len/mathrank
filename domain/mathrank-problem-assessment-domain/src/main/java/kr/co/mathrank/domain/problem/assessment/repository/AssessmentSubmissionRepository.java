package kr.co.mathrank.domain.problem.assessment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kr.co.mathrank.domain.problem.assessment.entity.AssessmentSubmission;

public interface AssessmentSubmissionRepository extends JpaRepository<AssessmentSubmission, Long> {
	@Query("""
		SELECT assessmentSubmission FROM AssessmentSubmission assessmentSubmission 
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
}
