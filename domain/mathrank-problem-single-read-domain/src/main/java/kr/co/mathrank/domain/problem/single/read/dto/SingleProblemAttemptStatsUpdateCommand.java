package kr.co.mathrank.domain.problem.single.read.dto;

import jakarta.validation.constraints.NotNull;

public record SingleProblemAttemptStatsUpdateCommand(
	@NotNull
	Long singleProblemId, // singleProblemId is different from problemId
	@NotNull
	Long memberId,
	@NotNull
	Boolean success,
	@NotNull
	Long firstTrySuccessCount,
	@NotNull
	Long totalAttemptedCount,
	@NotNull
	Long attemptedUserDistinctCount
) {
}
