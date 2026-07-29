package kr.co.mathrank.app.api.assessment;

import java.time.Instant;

import kr.co.mathrank.domain.problem.assessment.dto.AssessmentAttemptResult;

class Responses {
	record AssessmentRegisterResponse(String assessmentId) {
		static AssessmentRegisterResponse from(final Long assessmentId) {
			return new AssessmentRegisterResponse(String.valueOf(assessmentId));
		}
	}

	record AssessmentAttemptResponse(
		String attemptId,
		Integer attemptNumber,
		Instant startedAt,
		Instant serverNow,
		Instant answerUnlockedAt,
		Instant expiresAt,
		Boolean answerInputEnabled,
		Boolean rankEligible
	) {
		static AssessmentAttemptResponse from(final AssessmentAttemptResult result) {
			return new AssessmentAttemptResponse(
				String.valueOf(result.attemptId()),
				result.attemptNumber(),
				result.startedAt(),
				result.serverNow(),
				result.answerUnlockedAt(),
				result.expiresAt(),
				result.answerInputEnabled(),
				result.rankEligible()
			);
		}
	}

	record AssessmentAttemptSubmissionResponse(String submissionId) {
		static AssessmentAttemptSubmissionResponse from(final Long submissionId) {
			return new AssessmentAttemptSubmissionResponse(String.valueOf(submissionId));
		}
	}
}
