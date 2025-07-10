package kr.co.mathrank.domain.problem.dto;

import jakarta.validation.constraints.NotNull;

public record ProblemDeleteCommand(
	@NotNull
	Long problemId,
	@NotNull
	Long requestMemberId
) {
}
