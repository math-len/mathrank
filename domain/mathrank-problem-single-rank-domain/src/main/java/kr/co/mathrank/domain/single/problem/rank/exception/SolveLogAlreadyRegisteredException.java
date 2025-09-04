package kr.co.mathrank.domain.single.problem.rank.exception;

public class SolveLogAlreadyRegisteredException extends RankException{
	public SolveLogAlreadyRegisteredException() {
		super(8001, "풀이 기록이 이미 등록됐습니다");
	}
}
