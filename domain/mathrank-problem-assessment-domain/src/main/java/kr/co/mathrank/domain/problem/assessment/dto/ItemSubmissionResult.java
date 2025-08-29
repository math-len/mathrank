package kr.co.mathrank.domain.problem.assessment.dto;

import java.util.List;

import kr.co.mathrank.domain.problem.assessment.entity.AssessmentItemSubmission;

public record ItemSubmissionResult(
	Long problemId,
	Integer score,
	Boolean correct,
	Integer sequence,
	List<String> submittedAnswer,
	List<String> correctAnswer
) {
	public static ItemSubmissionResult from(final AssessmentItemSubmission itemSubmission) {
		return new ItemSubmissionResult(
			itemSubmission.getAssessmentItem().getProblemId(),
			itemSubmission.getAssessmentItem().getScore(),
			itemSubmission.getCorrect(),
			itemSubmission.getAssessmentItem().getSequence(),
			itemSubmission.getSubmittedAnswer(),
			itemSubmission.getRealAnswer());
	}
}
