package kr.co.mathrank.domain.problem.assessment.exception;

public class SubmissionDeniedException extends AssessmentException {
	public SubmissionDeniedException() {
		super(7010, "응시할 수 있는 문제집이 아닙니다.");
	}
}
