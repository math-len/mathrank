package kr.co.mathrank.domain.problem.assessment.repository;

import java.util.List;

import kr.co.mathrank.domain.problem.assessment.dto.AssessmentPageQuery;
import kr.co.mathrank.domain.problem.assessment.entity.Assessment;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentOrderDirection;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentOrder;

interface AssessmentQueryRepository {
	List<Assessment> query(AssessmentPageQuery query, final int pageSize, final int pageNumber, final AssessmentOrder assessmentOrder, final AssessmentOrderDirection direction);
	Long count(AssessmentPageQuery query);
}
