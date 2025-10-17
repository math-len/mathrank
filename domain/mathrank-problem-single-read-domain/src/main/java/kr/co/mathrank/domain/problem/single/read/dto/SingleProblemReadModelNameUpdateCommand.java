package kr.co.mathrank.domain.problem.single.read.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SingleProblemReadModelNameUpdateCommand(
	@NotNull
	Long singleProblemId,
	@NotBlank
	String singleProblemName
) {
}
