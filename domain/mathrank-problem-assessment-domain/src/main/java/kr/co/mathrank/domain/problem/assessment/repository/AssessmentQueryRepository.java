package kr.co.mathrank.domain.problem.assessment.repository;

import java.util.List;

import kr.co.mathrank.domain.problem.assessment.dto.AssessmentQuery;
import kr.co.mathrank.domain.problem.assessment.entity.Assessment;

interface AssessmentQueryRepository {
	List<Assessment> query(AssessmentQuery query, final int pageSize, final int pageNumber);
	Long count(AssessmentQuery query);
}
