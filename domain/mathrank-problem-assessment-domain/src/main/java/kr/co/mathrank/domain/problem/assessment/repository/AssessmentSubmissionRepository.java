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
		SELECT ASS FROM AssessmentSubmission ASS
		LEFT JOIN FETCH ASS.submittedItemAnswers assItems
		LEFT JOIN FETCH assItems.assessmentItem
		WHERE ASS.id = :assessmentSubmissionId
		""")
	Optional<AssessmentSubmission> find(@Param("assessmentSubmissionId") Long assessmentSubmissionId);
}
