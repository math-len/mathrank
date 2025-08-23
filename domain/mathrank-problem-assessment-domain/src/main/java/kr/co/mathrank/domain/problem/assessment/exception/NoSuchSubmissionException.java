package kr.co.mathrank.domain.problem.assessment.exception;

public class NoSuchSubmissionException extends AssessmentException{
	public NoSuchSubmissionException() {
		super(7007, "답안지를 찾을 수 없음");
	}
}
