package kr.co.mathrank.domain.problem.single.exception;

public class CannotFindChallengerException extends SingleProblemException {
	public CannotFindChallengerException() {
		super(5005, "풀이한 사람을 찾을 수 없음");
	}
}
