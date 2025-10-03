package kr.co.mathrank.domain.problem.single.exception;

public class CannotAccessSolutionException extends SingleProblemException {
	public CannotAccessSolutionException() {
		super(5007, "정답을 조회할 수 없음");
	}
}
