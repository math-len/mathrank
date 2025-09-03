package kr.co.mathrank.domain.problem.assessment.dto;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentPeriodType;
import kr.co.mathrank.domain.problem.core.Difficulty;

public record AssessmentPageQuery(
	String assessmentName,
	Difficulty difficulty,
	@NotNull
	AssessmentPeriodType periodType
) {
}
