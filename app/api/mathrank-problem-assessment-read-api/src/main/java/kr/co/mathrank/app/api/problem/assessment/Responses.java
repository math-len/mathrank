package kr.co.mathrank.app.api.problem.assessment;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import kr.co.mathrank.domain.problem.assessment.dto.AssessmentSubmissionRankResult;
import kr.co.mathrank.domain.problem.assessment.dto.SubmissionItemQueryResult;
import kr.co.mathrank.domain.problem.assessment.dto.SubmissionQueryResult;
import kr.co.mathrank.domain.problem.assessment.dto.SubmissionQueryResults;
import kr.co.mathrank.domain.problem.assessment.entity.EvaluationStatus;

class Responses {
	public record AssessmentSubmissionRankResponse(
		List<Integer> descendingScores,
		Integer score,
		Integer scoreRank,
		List<Long> ascendingElapsedTimeSeconds,
		Long elapsedTimeSeconds,
		Integer elapsedTimeRank,
		Integer totalUserCount
	) {
		public static AssessmentSubmissionRankResponse from(final AssessmentSubmissionRankResult result) {
			return new AssessmentSubmissionRankResponse(
				result.descendingScores(),
				result.score(),
				result.scoreRank(),
				result.ascendingElapsedTimes().stream()
					.map(Duration::toSeconds)
					.toList(),
				result.elapsedTime().toSeconds(),
				result.elapsedTimeRank(),
				result.totalUserCount()
			);
		}
	}

	public record AssessmentSubmissionQueryResponses(
		List<AssessmentSubmissionQueryResponse> responses
	) {
		public static AssessmentSubmissionQueryResponses from(final SubmissionQueryResults results) {
			return new AssessmentSubmissionQueryResponses(results.queryResults().stream()
				.map(AssessmentSubmissionQueryResponse::from)
				.toList());
		}
	}

	public record AssessmentSubmissionQueryResponse(
		Long submissionId,
		Long assessmentAverageScore,
		Long memberId,
		EvaluationStatus evaluationStatus,
		Integer totalScore,
		List<SubmissionItemQueryResult> itemSubmissionResults,
		LocalDateTime submittedAt,
		Long elapsedTimeSeconds
	) {
		public static AssessmentSubmissionQueryResponse from(final SubmissionQueryResult result) {
			return new AssessmentSubmissionQueryResponse(
				result.submissionId(),
				result.assessmentAverageScore(),
				result.memberId(),
				result.evaluationStatus(),
				result.totalScore(),
				result.submissionItemQueryResults(),
				result.submittedAt(),
				result.elapsedTime().toSeconds()
			);
		}

	}
}
