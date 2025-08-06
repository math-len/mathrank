package kr.co.mathrank.domain.problem.single.exception;

public class AlreadyRegisteredProblemException extends SingleProblemException {
	public AlreadyRegisteredProblemException() {
		super(5002, "이미 개별문제로 등록된 문제");
	}
}
