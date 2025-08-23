package kr.co.mathrank.domain.problem.assessment.exception;

public class NoSuchAssessmentSubmissionException extends AssessmentException{
	public NoSuchAssessmentSubmissionException() {
		super(7005, "답안지를 찾을 수 없음");
	}
}
