package kr.co.mathrank.domain.rank.exception;

public class SolverAlreadyExistException extends RankException {
	public SolverAlreadyExistException() {
		super(8002, "사용자가 이미 등록되어 있습니다.");
	}
}
