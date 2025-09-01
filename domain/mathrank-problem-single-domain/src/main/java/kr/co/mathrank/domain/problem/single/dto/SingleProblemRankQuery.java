package kr.co.mathrank.domain.problem.single.dto;

import jakarta.validation.constraints.NotNull;

public record SingleProblemRankQuery(
	@NotNull Long requestMemberId,
	@NotNull Long singleProblemId
) {
}
