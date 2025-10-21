package kr.co.mathrank.domain.rank.dto;

import jakarta.validation.constraints.NotNull;

public record SolverUpdateCommand(
	@NotNull
	Long memberId,
	@NotNull
	String solverName,
	String schoolCode
) {
}
