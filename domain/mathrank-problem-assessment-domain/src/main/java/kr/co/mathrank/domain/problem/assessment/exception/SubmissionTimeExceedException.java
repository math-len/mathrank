package kr.co.mathrank.domain.problem.assessment.exception;

public class SubmissionTimeExceedException extends AssessmentException {
	public SubmissionTimeExceedException() {
		super(7006, "시험 시간을 초과했습니다");
	}
}
