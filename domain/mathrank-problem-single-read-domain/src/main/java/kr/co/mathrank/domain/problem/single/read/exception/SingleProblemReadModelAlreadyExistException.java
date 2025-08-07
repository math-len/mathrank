package kr.co.mathrank.domain.problem.single.read.exception;

public class SingleProblemReadModelAlreadyExistException extends SingleProblemReadException {
	public SingleProblemReadModelAlreadyExistException() {
		super(6002, "이미 저장된 개별 문제");
	}
}
