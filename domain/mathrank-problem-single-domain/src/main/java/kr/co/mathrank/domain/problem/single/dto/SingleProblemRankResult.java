package kr.co.mathrank.domain.problem.single.dto;

import java.time.Duration;
import java.util.List;

public record SingleProblemRankResult(
	Long distinctUserCount,
	Integer myRank,
	Duration myElapsedTime,
	List<Duration> elapsedTimes,
	Duration averageElapsedTime
) {
}
