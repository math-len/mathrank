package kr.co.mathrank.domain.problem.assessment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kr.co.mathrank.domain.problem.assessment.entity.Assessment;

public interface AssessmentRepository extends JpaRepository<Assessment, Long>, AssessmentQueryRepository{
	@Query("""
		SELECT ass FROM Assessment ass
				 LEFT JOIN FETCH ass.assessmentItems
						  WHERE ass.id = :assessmentId
		""")
	Optional<Assessment> findWithItems(@Param("assessmentId") final Long assessmentId);
}
