package kr.co.mathrank.app.api.problem.exam;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import kr.co.mathrank.domain.problem.assessment.dto.AssessmentSubmissionRankResult;
import kr.co.mathrank.domain.problem.assessment.dto.SubmissionItemQueryResult;
import kr.co.mathrank.domain.problem.assessment.dto.SubmissionQueryResult;
import kr.co.mathrank.domain.problem.assessment.dto.SubmissionQueryResults;
import kr.co.mathrank.domain.problem.assessment.entity.EvaluationStatus;

class Responses {
	public record ExamSubmissionRankResponse(
		List<Integer> descendingScores,
		Integer score,
		Integer scoreRank,
		List<Long> ascendingElapsedTimeSeconds,
		Long elapsedTimeSeconds,
		Integer elapsedTimeRank
	) {
		public static ExamSubmissionRankResponse from(final AssessmentSubmissionRankResult result) {
			return new ExamSubmissionRankResponse(
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

	public record ExamSubmissionQueryResponses(
		List<ExamSubmissionQueryResponse> responses
	) {
		public static ExamSubmissionQueryResponses from(final SubmissionQueryResults results) {
			return new ExamSubmissionQueryResponses(results.queryResults().stream()
				.map(ExamSubmissionQueryResponse::from)
				.toList());
		}
	}

	public record ExamSubmissionQueryResponse(
		Long submissionId,
		Long assessmentAverageScore,
		Long memberId,
		EvaluationStatus evaluationStatus,
		Integer totalScore,
		List<SubmissionItemQueryResult> itemSubmissionResults,
		LocalDateTime submittedAt,
		Long elapsedTimeSeconds
	) {
		public static ExamSubmissionQueryResponse from(final SubmissionQueryResult result) {
			return new ExamSubmissionQueryResponse(
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
