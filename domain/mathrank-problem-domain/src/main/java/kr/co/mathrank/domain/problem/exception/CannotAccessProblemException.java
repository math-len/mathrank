package kr.co.mathrank.domain.problem.exception;

public class CannotAccessProblemException extends ProblemException {
	public CannotAccessProblemException() {
		super(3002, "문제에 접근할 수 있는 권한 부재");
	}
}
