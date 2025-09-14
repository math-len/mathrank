package kr.co.mathrank.app.api.problem.assessment;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import kr.co.mathrank.domain.problem.assessment.dto.AssessmentDetailReadModelResult;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentItemReadModelDetailResult;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentPageQueryResult;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentSubmissionRankResult;
import kr.co.mathrank.domain.problem.assessment.dto.CourseDetailResult;
import kr.co.mathrank.domain.problem.assessment.dto.SubmissionItemQueryResult;
import kr.co.mathrank.domain.problem.assessment.dto.SubmissionQueryResult;
import kr.co.mathrank.domain.problem.assessment.dto.SubmissionQueryResults;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentPeriodType;
import kr.co.mathrank.domain.problem.assessment.entity.EvaluationStatus;
import kr.co.mathrank.domain.problem.core.AnswerType;
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
		String submissionId,
		Long assessmentAverageScore,
		String memberId,
		EvaluationStatus evaluationStatus,
		Integer totalScore,
		List<SubmissionItemQueryResult> itemSubmissionResults,
		LocalDateTime submittedAt,
		Long elapsedTimeSeconds
	) {
		public static AssessmentSubmissionQueryResponse from(final SubmissionQueryResult result) {
			return new AssessmentSubmissionQueryResponse(
				String.valueOf(result.submissionId()),
				result.assessmentAverageScore(),
				String.valueOf(result.memberId()),
				result.evaluationStatus(),
				result.totalScore(),
				result.submissionItemQueryResults(),
				result.submittedAt(),
				result.elapsedTime().toSeconds()
			);
		}
	}

	public record AssessmentDetailResponse(
		String assessmentId,
		List<AssessmentItemReadModelDetailResponse> itemDetails,
		String registeredMemberId,
		String assessmentName,
		Long distinctUserCount,
		LocalDateTime createdAt,
		Difficulty difficulty,
		Long minutes
	) {
		public static AssessmentDetailResponse from(AssessmentDetailReadModelResult result) {
			return new AssessmentDetailResponse(
				String.valueOf(result.assessmentId()),
				result.itemDetails().stream()
					.map(AssessmentItemReadModelDetailResponse::from)
					.toList(),
				String.valueOf(result.registeredMemberId()),
				result.assessmentName(),
				result.distinctUserCount(),
				result.createdAt(),
				result.difficulty(),
				result.minutes()
			);
		}
	}

	public record AssessmentItemReadModelDetailResponse(
		String problemId,
		String problemImage,
		String memberId,
		Integer score,
		CourseDetailResult courseDetailResult,
		Difficulty difficulty,
		AnswerType type,
		String schoolCode,
		LocalDateTime createdAt,
		Integer year
	) {
		public static AssessmentItemReadModelDetailResponse from(final AssessmentItemReadModelDetailResult result) {
			return new AssessmentItemReadModelDetailResponse(
				String.valueOf(result.problemId()),
				result.problemImage(),
				String.valueOf(result.memberId()),
				result.score(),
				result.courseDetailResult(),
				result.difficulty(),
				result.type(),
				result.schoolCode(),
				result.createdAt(),
				result.year()
			);
		}

	}

	public record AssessmentPageResponse(
		String assessmentId,
		String memberId,
		String assessmentName,
		Long distinctUserCount,
		LocalDateTime createdAt,
		Difficulty difficulty,
		Long minutes,
		Boolean solved
	) {
		public static AssessmentPageResponse from(AssessmentPageQueryResult result) {
			return new AssessmentPageResponse(
				String.valueOf(result.assessmentId()),
				String.valueOf(result.memberId()),
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
