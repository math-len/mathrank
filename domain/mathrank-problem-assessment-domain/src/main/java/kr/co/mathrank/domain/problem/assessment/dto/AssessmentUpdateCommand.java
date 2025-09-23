package kr.co.mathrank.domain.problem.assessment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AssessmentUpdateCommand(
	@NotNull
	Long assessmentId,
	@NotBlank
	String assessmentName
) {
}
