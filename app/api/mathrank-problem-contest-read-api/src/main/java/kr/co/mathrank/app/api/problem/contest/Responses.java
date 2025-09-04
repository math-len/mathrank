package kr.co.mathrank.app.api.problem.contest;

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
import kr.co.mathrank.domain.problem.assessment.entity.EvaluationStatus;
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
		Long submissionId,
		Long contestAverageScore,
		Long memberId,
		EvaluationStatus evaluationStatus,
		Integer totalScore,
		List<SubmissionItemQueryResult> itemSubmissionResults,
		LocalDateTime submittedAt,
		Long elapsedTimeSeconds
	) {
		public static ContestSubmissionQueryResponse from(final SubmissionQueryResult result) {
			return new ContestSubmissionQueryResponse(
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

	public record ContestDetailResponse(
		Long contestId,
		List<AssessmentItemReadModelDetailResult> itemDetails,
		Long registeredMemberId,
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
				result.assessmentId(),
				result.itemDetails(),
				result.registeredMemberId(),
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

	public record ContestPageResponse(
		Long contestId,
		Long memberId,
		String contestName,
		Long distinctUserCount,
		LocalDateTime createdAt,
		Difficulty difficulty,
		Long minutes,
		LocalDateTime startAt,
		LocalDateTime endAt
	) {
		public static ContestPageResponse from(AssessmentPageQueryResult result) {
			return new ContestPageResponse(
				result.assessmentId(),
				result.memberId(),
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
}
