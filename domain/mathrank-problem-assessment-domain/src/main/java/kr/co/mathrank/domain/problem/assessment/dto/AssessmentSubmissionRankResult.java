package kr.co.mathrank.domain.problem.assessment.dto;

import java.time.Duration;
import java.util.List;

public record AssessmentSubmissionRankResult(
	List<Integer> descendingScores,
	Integer score,
	Integer scoreRank,
	List<Duration> ascendingElapsedTimes,
	Duration elapsedTime,
	Integer elapsedTimeRank
) {
}
