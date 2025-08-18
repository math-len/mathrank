package kr.co.mathrank.domain.problem.assessment.dto;

import jakarta.validation.constraints.NotNull;

public record AssessmentItemRegisterCommand(
	@NotNull
	Long problemId,
	@NotNull
	Integer score
) {
}
