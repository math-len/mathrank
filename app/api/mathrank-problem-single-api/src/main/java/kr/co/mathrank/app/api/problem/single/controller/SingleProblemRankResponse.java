package kr.co.mathrank.app.api.problem.single.controller;

import java.time.Duration;
import java.util.List;

import kr.co.mathrank.domain.problem.single.dto.SingleProblemRankResult;

public record SingleProblemRankResponse(
	Long distinctUserCount,
	Integer myRank,
	Long myElapsedTimeSecond,
	List<Long> elapsedTimeSeconds,
	Long averageElapsedTimeSecond
) {
	public static SingleProblemRankResponse from(final SingleProblemRankResult result) {
		return new SingleProblemRankResponse(
			result.distinctUserCount(),
			result.myRank(),
			result.myElapsedTime().getSeconds(),
			result.elapsedTimes().stream()
				.map(Duration::getSeconds)
				.toList(),
			result.averageElapsedTime().getSeconds()
		);
	}
}
