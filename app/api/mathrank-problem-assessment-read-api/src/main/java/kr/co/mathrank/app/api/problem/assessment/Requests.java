package kr.co.mathrank.app.api.problem.assessment;

import kr.co.mathrank.domain.problem.assessment.dto.AssessmentPageQuery;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentPeriodType;
import kr.co.mathrank.domain.problem.core.Difficulty;

public class Requests {
	record AssessmentPageQueryRequest(
		String assessmentName,
		Difficulty difficulty
	){
		AssessmentPageQuery toQuery() {
			return new AssessmentPageQuery(
				assessmentName,
				difficulty,
				AssessmentPeriodType.UNLIMITED
			);
		}
	}
}
