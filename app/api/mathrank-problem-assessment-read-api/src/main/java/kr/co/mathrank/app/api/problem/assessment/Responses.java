package kr.co.mathrank.app.api.problem.assessment;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import kr.co.mathrank.domain.problem.assessment.dto.AssessmentSubmissionQueryResult;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentSubmissionQueryResults;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentSubmissionRankResult;
import kr.co.mathrank.domain.problem.assessment.dto.ItemSubmissionResult;
import kr.co.mathrank.domain.problem.assessment.entity.EvaluationStatus;

class Responses {
	public record AssessmentSubmissionRankResponse(
		List<Integer> descendingScores,
		Integer score,
		Integer scoreRank,
		List<Long> ascendingElapsedTimeSeconds,
		Long elapsedTimeSeconds,
		Integer elapsedTimeRank
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
				result.elapsedTimeRank()
			);
		}
	}

	public record AssessmentSubmissionQueryResponses(
		List<AssessmentSubmissionQueryResponse> responses
	) {
		public static AssessmentSubmissionQueryResponses from(final AssessmentSubmissionQueryResults results) {
			return new AssessmentSubmissionQueryResponses(results.queryResults().stream()
				.map(AssessmentSubmissionQueryResponse::from)
				.toList());
		}
	}

	public record AssessmentSubmissionQueryResponse(
		Long assessmentId,
		Long assessmentAverageScore,
		Long memberId,
		EvaluationStatus evaluationStatus,
		Integer totalScore,
		List<ItemSubmissionResult> itemSubmissionResults,
		LocalDateTime submittedAt,
		Long elapsedTimeSeconds
	) {
		public static AssessmentSubmissionQueryResponse from(final AssessmentSubmissionQueryResult result) {
			return new AssessmentSubmissionQueryResponse(
				result.assessmentId(),
				result.assessmentAverageScore(),
				result.memberId(),
				result.evaluationStatus(),
				result.totalScore(),
				result.itemSubmissionResults(),
				result.submittedAt(),
				result.elapsedTime().toSeconds()
			);
		}

	}
}
