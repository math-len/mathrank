package kr.co.mathrank.domain.problem.single.dto;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import kr.co.mathrank.domain.problem.single.entity.ChallengeLog;

public record SingleProblemChallengeLogResult(
	Long challengeLogId,
	Boolean success,
	LocalDateTime submittedAt,
	Duration elapsedTime,
	List<String> submittedAnswer,
	List<String> correctAnswer
) {
	public static SingleProblemChallengeLogResult from(final ChallengeLog challengeLog) {
		return new SingleProblemChallengeLogResult(
			challengeLog.getId(),
			challengeLog.getSuccess(),
			challengeLog.getChallengedAt(),
			challengeLog.getElapsedTime(),
			challengeLog.getSubmittedAnswer(),
			challengeLog.getCorrectAnswer()
		);
	}
}
