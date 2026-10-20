package kr.co.mathrank.domain.problem.assessment.exception;

public class SubmissionDeniedException extends AssessmentException {
	public SubmissionDeniedException() {
		super(7010, "응시 기간이 아닙니다.");
	}
}
