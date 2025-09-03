package kr.co.mathrank.domain.problem.assessment.dto;

import java.time.LocalDateTime;
import java.util.List;

import kr.co.mathrank.domain.problem.core.Difficulty;

public record AssessmentDetailReadModelResult(
	Long assessmentId,
	List<AssessmentItemReadModelDetailResult> itemDetails,
	Long registeredMemberId,
	String assessmentName,
	Long distinctUserCount,
	LocalDateTime createdAt,
	Difficulty difficulty,
	Long minutes,
	LocalDateTime startAt,
	LocalDateTime endAt
) {
	public static AssessmentDetailReadModelResult from(final AssessmentDetailResult detailResult, final List<AssessmentItemReadModelDetailResult> itemReadModelDetailResults) {
		return new AssessmentDetailReadModelResult(
			detailResult.assessmentId(),
			itemReadModelDetailResults,
			detailResult.memberId(),
			detailResult.assessmentName(),
			detailResult.distinctUserCount(),
			detailResult.createdAt(),
			detailResult.difficulty(),
			detailResult.minutes(),
			detailResult.startAt(),
			detailResult.endAt()
		);
	}
}
