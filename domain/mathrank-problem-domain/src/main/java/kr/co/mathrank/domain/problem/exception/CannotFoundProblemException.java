package kr.co.mathrank.domain.problem.exception;

public class CannotFoundProblemException extends ProblemException {
	private static final String FORMAT = "존재하지 않는 문제: %s";

	public CannotFoundProblemException(final Long problemId) {
		super(3001, FORMAT.formatted(problemId));
	}
}
