package kr.co.mathrank.app.api.problem.single.controller;

import java.time.LocalDateTime;
import java.util.List;

import kr.co.mathrank.domain.problem.single.dto.SingleProblemChallengeLogResult;

public record SingleProblemChallengeLogResponse(
	Long challengeLogId,
	Boolean success,
	LocalDateTime submittedAt,
	Long elapsedTimeSeconds,
	List<String> submittedAnswer,
	List<String> correctAnswer
) {
	public static SingleProblemChallengeLogResponse from(SingleProblemChallengeLogResult result) {
		return new SingleProblemChallengeLogResponse(
			result.challengeLogId(),
			result.success(),
			result.submittedAt(),
			result.elapsedTime().toSeconds(),
			result.submittedAnswer(),
			result.correctAnswer()
		);
	}
}
