package kr.co.mathrank.domain.problem.single.dto;

import java.time.Duration;
import java.util.List;

public record SingleProblemStatisticsResult(
	Long singleProblemId,
	List<Duration> elapsedTimes,
	Duration averageElapsedTime,
	Integer firstTrySucceedUserCount,
	Long distinctUserCount
) {
}
