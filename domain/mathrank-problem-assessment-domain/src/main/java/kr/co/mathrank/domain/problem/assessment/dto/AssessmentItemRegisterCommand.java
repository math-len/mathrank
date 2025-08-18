package kr.co.mathrank.domain.problem.assessment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AssessmentItemRegisterCommand(
	@NotNull
	Long problemId,
	@NotNull
	@Positive
	Integer score
) {
}
