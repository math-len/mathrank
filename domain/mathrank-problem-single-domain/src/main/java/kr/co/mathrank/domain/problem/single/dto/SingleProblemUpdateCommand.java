package kr.co.mathrank.domain.problem.single.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SingleProblemUpdateCommand(
	@NotNull
	Long singleProblemId,
	@NotBlank
	String singleProblemName
) {
}
