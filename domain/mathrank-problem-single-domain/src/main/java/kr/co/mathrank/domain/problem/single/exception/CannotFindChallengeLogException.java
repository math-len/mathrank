package kr.co.mathrank.domain.problem.single.exception;

public class CannotFindChallengeLogException extends SingleProblemException{
	public CannotFindChallengeLogException() {
		super(5006, "풀이 기록을 찾을 수 없음");
	}
}
