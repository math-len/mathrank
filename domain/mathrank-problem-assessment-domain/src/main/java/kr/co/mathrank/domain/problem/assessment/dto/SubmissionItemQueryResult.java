package kr.co.mathrank.domain.problem.assessment.dto;

import java.util.List;

import kr.co.mathrank.domain.problem.assessment.entity.AssessmentItemSubmission;

public record SubmissionItemQueryResult(
	Long problemId,
	Boolean correct,
	Integer sequence,
	List<String> submittedAnswer,
	List<String> correctAnswer
) {
	public static SubmissionItemQueryResult from(final AssessmentItemSubmission itemSubmission) {
		return new SubmissionItemQueryResult(
			itemSubmission.getAssessmentItem().getProblemId(),
			itemSubmission.getCorrect(),
			itemSubmission.getAssessmentItem().getSequence(),
			itemSubmission.getSubmittedAnswer(),
			itemSubmission.getRealAnswer());
	}
}
