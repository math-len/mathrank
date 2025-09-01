package kr.co.mathrank.domain.problem.assessment.repository;

import java.util.List;

import kr.co.mathrank.domain.problem.assessment.dto.AssessmentQuery;
import kr.co.mathrank.domain.problem.assessment.entity.Assessment;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentOrder;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentOrderDirection;

interface AssessmentQueryRepository {
	List<Assessment> query(AssessmentQuery query, final int pageSize, final int pageNumber, final AssessmentOrder assessmentOrder, final AssessmentOrderDirection direction);
	Long count(AssessmentQuery query);
}
