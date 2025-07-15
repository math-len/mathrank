package kr.co.mathrank.domain.problem.exception;

import kr.co.mathrank.common.exception.MathRankException;

public class ProblemException extends MathRankException {
	ProblemException(final int code, final String message) {
		super(code, message);
	}
}
