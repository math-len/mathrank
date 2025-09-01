package kr.co.mathrank.domain.problem.assessment.dto;

import java.time.LocalDateTime;

import kr.co.mathrank.domain.problem.assessment.entity.Assessment;
import kr.co.mathrank.domain.problem.core.Difficulty;

public record AssessmentPageQueryResult(
	Long assessmentId,
	Long memberId,
	String assessmentName,
	Long distinctUserCount,
	LocalDateTime createdAt,
	Difficulty difficulty,
	Long minutes
) {
	public static AssessmentPageQueryResult from(Assessment assessment) {
		return new AssessmentPageQueryResult(
			assessment.getId(),
			assessment.getRegisterMemberId(),
			assessment.getAssessmentName(),
			assessment.getDistinctTriedMemberCount(),
			assessment.getCreatedAt(),
			assessment.getDifficulty(),
			assessment.getAssessmentDuration().toMinutes()
		);
	}
}
