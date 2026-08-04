package kr.co.mathrank.domain.problem.assessment.dto;

import java.time.Instant;

import kr.co.mathrank.domain.problem.assessment.entity.AssessmentAttempt;

public record AssessmentAttemptResult(
	Long attemptId,
	Integer attemptNumber,
	Instant startedAt,
	Instant serverNow,
	Instant answerUnlockedAt,
	Instant expiresAt,
	Boolean answerInputEnabled,
	Boolean rankEligible,
	Boolean newlyCreated
) {
	public static AssessmentAttemptResult from(
		final AssessmentAttempt attempt,
		final Instant serverNow,
		final boolean rankEligible,
		final boolean newlyCreated
	) {
		return new AssessmentAttemptResult(
			attempt.getId(),
			attempt.getAttemptNumber(),
			attempt.getStartedAt(),
			serverNow,
			attempt.getAnswerUnlockedAt(),
			attempt.getExpiresAt(),
			attempt.isAnswerInputEnabledAt(serverNow),
			rankEligible,
			newlyCreated
		);
	}
}
