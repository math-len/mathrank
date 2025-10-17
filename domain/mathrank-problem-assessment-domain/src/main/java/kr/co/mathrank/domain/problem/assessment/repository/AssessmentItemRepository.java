package kr.co.mathrank.domain.problem.assessment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kr.co.mathrank.domain.problem.assessment.entity.Assessment;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentItem;

public interface AssessmentItemRepository extends JpaRepository<AssessmentItem, Long> {
	@Query("""
SELECT assItem FROM AssessmentItem assItem
LEFT JOIN FETCH assItem.assessment
WHERE assItem.problemId = :problemId
""")
	List<AssessmentItem> findAllContainsProblemId(@Param("problemId") final Long problemId);
}
