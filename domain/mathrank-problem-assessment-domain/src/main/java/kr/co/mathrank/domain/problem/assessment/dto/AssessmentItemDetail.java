package kr.co.mathrank.domain.problem.assessment.dto;

import kr.co.mathrank.domain.problem.assessment.entity.AssessmentItem;

public record AssessmentItemDetail(
	Long problemId,
	Integer sequence,
	Integer score
) {
	public static AssessmentItemDetail from(AssessmentItem assessmentItem) {
		return new AssessmentItemDetail(
			assessmentItem.getProblemId(),
			assessmentItem.getSequence(),
			assessmentItem.getScore()
		);
	}
}
