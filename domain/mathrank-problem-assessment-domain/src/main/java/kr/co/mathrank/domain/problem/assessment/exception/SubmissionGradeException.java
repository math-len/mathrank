package kr.co.mathrank.domain.problem.assessment.exception;

public class SubmissionGradeException extends AssessmentException {
	public SubmissionGradeException() {
		super(7008, "채점할 수 없습니다.");
	}
}
