package kr.co.mathrank.app.api.problem.contest;

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
import kr.co.mathrank.domain.problem.assessment.entity.EvaluationStatus;
import kr.co.mathrank.domain.problem.core.AnswerType;
import kr.co.mathrank.domain.problem.core.Difficulty;

class Responses {
	public record ContestSubmissionRankResponse(
		List<Integer> descendingScores,
		Integer score,
		Integer scoreRank,
		List<Long> ascendingElapsedTimeSeconds,
		Long elapsedTimeSeconds,
		Integer elapsedTimeRank
	) {
		public static ContestSubmissionRankResponse from(final AssessmentSubmissionRankResult result) {
			return new ContestSubmissionRankResponse(
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

	public record ContestSubmissionQueryResponses(
		List<ContestSubmissionQueryResponse> responses
	) {
		public static ContestSubmissionQueryResponses from(final SubmissionQueryResults results) {
			return new ContestSubmissionQueryResponses(results.queryResults().stream()
				.map(ContestSubmissionQueryResponse::from)
				.toList());
		}
	}

	public record ContestSubmissionQueryResponse(
		String submissionId,
		Long contestAverageScore,
		String memberId,
		EvaluationStatus evaluationStatus,
		Integer totalScore,
		List<SubmissionItemQueryResult> itemSubmissionResults,
		LocalDateTime submittedAt,
		Long elapsedTimeSeconds
	) {
		public static ContestSubmissionQueryResponse from(final SubmissionQueryResult result) {
			return new ContestSubmissionQueryResponse(
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

	public record ContestDetailResponse(
		String contestId,
		List<ContestItemReadModelDetailResponse> itemDetails,
		String registeredMemberId,
		String contestName,
		Long distinctUserCount,
		LocalDateTime createdAt,
		Difficulty difficulty,
		Long minutes,
		LocalDateTime startAt,
		LocalDateTime endAt
	) {
		public static ContestDetailResponse from(AssessmentDetailReadModelResult result) {
			return new ContestDetailResponse(
				String.valueOf(result.assessmentId()),
				result.itemDetails().stream()
					.map(ContestItemReadModelDetailResponse::from)
					.toList(),
				String.valueOf(result.registeredMemberId()),
				result.assessmentName(),
				result.distinctUserCount(),
				result.createdAt(),
				result.difficulty(),
				result.minutes(),
				result.startAt(),
				result.endAt()
			);
		}
	}

	public record ContestItemReadModelDetailResponse(
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
		public static ContestItemReadModelDetailResponse from(final AssessmentItemReadModelDetailResult result) {
			return new ContestItemReadModelDetailResponse(
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

	public record ContestPageResponse(
		String contestId,
		String memberId,
		String contestName,
		Long distinctUserCount,
		LocalDateTime createdAt,
		Difficulty difficulty,
		Long minutes,
		LocalDateTime startAt,
		LocalDateTime endAt,
		Boolean solved
	) {
		public static ContestPageResponse from(AssessmentPageQueryResult result) {
			return new ContestPageResponse(
				String.valueOf(result.assessmentId()),
				String.valueOf(result.memberId()),
				result.assessmentName(),
				result.distinctUserCount(),
				result.createdAt(),
				result.difficulty(),
				result.minutes(),
				result.startAt(),
				result.endAt(),
				result.solved()
			);
		}
	}
}
