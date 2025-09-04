package kr.co.mathrank.domain.single.problem.rank.dto;

import jakarta.validation.constraints.NotNull;

public record SolveLogRegisterCommand(
	@NotNull Long singleProblemId,
	@NotNull Long problemId,
	@NotNull Long memberId,
	@NotNull Boolean success
) {
}
