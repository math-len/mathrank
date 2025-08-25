package kr.co.mathrank.domain.problem.assessment.dto;

import kr.co.mathrank.domain.problem.assessment.entity.Assessment;

public record AssessmentQueryResult(
	Long assessmentId,
	Long memberId,
	String assessmentName
) {
	public static AssessmentQueryResult from(Assessment assessment) {
		return new AssessmentQueryResult(
			assessment.getId(),
			assessment.getRegisterMemberId(),
			assessment.getAssessmentName()
		);
	}
}
