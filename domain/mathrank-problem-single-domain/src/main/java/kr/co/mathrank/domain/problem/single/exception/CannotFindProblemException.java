package kr.co.mathrank.domain.problem.single.exception;

public class CannotFindProblemException extends SingleProblemException {
	public CannotFindProblemException() {
		super(5004, "등록할 문제를 찾을 수 없음");
	}
}
