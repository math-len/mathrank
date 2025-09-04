package kr.co.mathrank.domain.rank.dto;

import jakarta.validation.constraints.NotNull;

public record SolveLogRegisterCommand(
	@NotNull Long singleProblemId,
	@NotNull Long problemId,
	@NotNull Long memberId,
	@NotNull Boolean success
) {
}
