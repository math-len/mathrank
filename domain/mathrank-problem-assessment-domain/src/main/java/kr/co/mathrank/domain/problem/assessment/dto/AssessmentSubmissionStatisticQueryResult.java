package kr.co.mathrank.domain.problem.assessment.dto;

import java.time.Duration;
import java.util.List;

public record AssessmentSubmissionStatisticQueryResult(
	Long assessmentId,
	List<Integer> descendingScores,
	List<Duration> ascendingElapsedTimes
) {
}
