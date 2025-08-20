package kr.co.mathrank.domain.problem.assessment.exception;

public class AssessmentSubmissionRegisterException extends AssessmentException{
	public AssessmentSubmissionRegisterException() {
		super(7004, "제출된 답안의 개수가 시험 문항 수와 일치하지 않습니다.");
	}
}
