package kr.co.mathrank.domain.problem.assessment.exception;

public class NoSuchAssessmentException extends AssessmentException{
	public NoSuchAssessmentException() {
		super(7005, "시험지를 찾을 수 없습니다.");
	}
}
