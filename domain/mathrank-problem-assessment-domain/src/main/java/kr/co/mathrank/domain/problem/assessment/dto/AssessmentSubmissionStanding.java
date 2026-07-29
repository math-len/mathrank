package kr.co.mathrank.domain.problem.assessment.dto;

import java.time.Duration;

public record AssessmentSubmissionStanding(
	Integer totalScore,
	Duration elapsedTime
) {
}
