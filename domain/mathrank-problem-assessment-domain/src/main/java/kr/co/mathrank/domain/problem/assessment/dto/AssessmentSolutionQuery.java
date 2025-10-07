package kr.co.mathrank.domain.problem.assessment.dto;

import jakarta.validation.constraints.NotNull;

public record AssessmentSolutionQuery(
	@NotNull
	Long assessmentId,
	@NotNull
	Long requestMemberId
) {
}
