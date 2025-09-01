package kr.co.mathrank.domain.problem.assessment.exception;

public class SubmissionNotEvaluatedException extends AssessmentException {
	public SubmissionNotEvaluatedException() {
		super(7009, "답안지가 채점되지 않았습니다.");
	}
}
