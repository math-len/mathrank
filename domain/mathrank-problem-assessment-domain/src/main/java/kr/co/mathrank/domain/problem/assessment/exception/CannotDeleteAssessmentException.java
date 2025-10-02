package kr.co.mathrank.domain.problem.assessment.exception;

public class CannotDeleteAssessmentException extends AssessmentException {
	public CannotDeleteAssessmentException() {
		super(7011, "삭제할 수 없습니다.");
	}
}
