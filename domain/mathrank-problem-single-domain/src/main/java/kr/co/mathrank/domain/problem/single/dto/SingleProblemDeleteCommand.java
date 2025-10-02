package kr.co.mathrank.domain.problem.single.dto;

import jakarta.validation.constraints.NotNull;

public record SingleProblemDeleteCommand(
	@NotNull
	Long singleProblemId
) {
}
