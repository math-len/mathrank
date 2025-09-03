package kr.co.mathrank.domain.problem.assessment.dto;

import java.time.LocalDateTime;
import java.util.List;

import kr.co.mathrank.domain.problem.assessment.entity.Assessment;
import kr.co.mathrank.domain.problem.core.Difficulty;

public record AssessmentDetailResult(
	Long assessmentId,
	List<AssessmentItemDetail> itemDetails,
	Long memberId,
	String assessmentName,
	Long distinctUserCount,
	LocalDateTime createdAt,
	Difficulty difficulty,
	Long minutes,
	LocalDateTime startAt,
	LocalDateTime endAt
) {
	public static AssessmentDetailResult from(final Assessment assessment) {
		return new AssessmentDetailResult(
			assessment.getId(),
			assessment.getAssessmentItems().stream()
				.map(AssessmentItemDetail::from)
				.toList(),
			assessment.getRegisterMemberId(),
			assessment.getAssessmentName(),
			assessment.getDistinctTriedMemberCount(),
			assessment.getCreatedAt(),
			assessment.getDifficulty(),
			assessment.getAssessmentDuration().toMinutes(),
			assessment.getAssessmentSubmissionPeriod().getStartAt(),
			assessment.getAssessmentSubmissionPeriod().getEndAt()
		);
	}
}
