package kr.co.mathrank.domain.problem.assessment.exception;

public class AssessmentItemCountNotMatchException extends AssessmentException {
	public AssessmentItemCountNotMatchException() {
		super(7003, "문제 갯수와 점수갯수가 일치하지 않음");
	}
}
