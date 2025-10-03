package kr.co.mathrank.domain.problem.single.dto;

import jakarta.validation.constraints.NotNull;

public record SingleProblemSolutionQuery(
	@NotNull
	Long singleProblemId,
	@NotNull
	Long requestMemberId
) {
}
