package kr.co.mathrank.domain.problem.single.read.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

public record SingleProblemAttemptStatsUpdateCommand(
	@NotNull
	Long singleProblemId, // singleProblemId is different from problemId
	@NotNull
	Long firstTrySuccessCount,
	@NotNull
	Long totalAttemptedCount,
	@NotNull
	Long attemptedUserDistinctCount
) {
}
