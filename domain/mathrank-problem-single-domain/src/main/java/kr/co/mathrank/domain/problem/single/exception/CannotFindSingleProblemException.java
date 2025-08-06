package kr.co.mathrank.domain.problem.single.exception;

public class CannotFindSingleProblemException extends SingleProblemException {
	public CannotFindSingleProblemException() {
		super(5003, "개별문제를 찾을 수 없음");
	}
}
