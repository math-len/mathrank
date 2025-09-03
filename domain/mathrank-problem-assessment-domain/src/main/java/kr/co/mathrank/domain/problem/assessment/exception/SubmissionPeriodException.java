package kr.co.mathrank.domain.problem.assessment.exception;

public class SubmissionPeriodException extends AssessmentException {
	public SubmissionPeriodException() {
		super(7010, "응시할 수 있는 기한이 아닙니다.");
	}
}
