package kr.co.mathrank.app.api.problem.assessment;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import kr.co.mathrank.domain.problem.assessment.dto.AssessmentDetailReadModelResult;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentItemReadModelDetailResult;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentPageQueryResult;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentSubmissionRankResult;
import kr.co.mathrank.domain.problem.assessment.dto.SubmissionItemQueryResult;
import kr.co.mathrank.domain.problem.assessment.dto.SubmissionQueryResult;
import kr.co.mathrank.domain.problem.assessment.dto.SubmissionQueryResults;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentPeriodType;
import kr.co.mathrank.domain.problem.assessment.entity.EvaluationStatus;
import kr.co.mathrank.domain.problem.core.Difficulty;

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

	public record AssessmentDetailResponse(
		Long assessmentId,
		List<AssessmentItemReadModelDetailResult> itemDetails,
		Long registeredMemberId,
		String assessmentName,
		Long distinctUserCount,
		LocalDateTime createdAt,
		Difficulty difficulty,
		Long minutes
	) {
		public static AssessmentDetailResponse from(AssessmentDetailReadModelResult result) {
			return new AssessmentDetailResponse(
				result.assessmentId(),
				result.itemDetails(),
				result.registeredMemberId(),
				result.assessmentName(),
				result.distinctUserCount(),
				result.createdAt(),
				result.difficulty(),
				result.minutes()
			);
		}
	}

	public record AssessmentPageResponse(
		Long assessmentId,
		Long memberId,
		String assessmentName,
		Long distinctUserCount,
		LocalDateTime createdAt,
		Difficulty difficulty,
		Long minutes,
		Boolean solved
	) {
		public static AssessmentPageResponse from(AssessmentPageQueryResult result) {
			return new AssessmentPageResponse(
				result.assessmentId(),
				result.memberId(),
				result.assessmentName(),
				result.distinctUserCount(),
				result.createdAt(),
				result.difficulty(),
				result.minutes(),
				result.solved()
			);
		}
	}
}
