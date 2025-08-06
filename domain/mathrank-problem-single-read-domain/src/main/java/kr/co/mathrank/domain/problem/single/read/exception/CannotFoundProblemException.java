package kr.co.mathrank.domain.problem.single.read.exception;

public class CannotFoundProblemException extends SingleProblemReadException {
	public CannotFoundProblemException() {
		super(6001, "문제를 찾을 수 없음");
	}
}
