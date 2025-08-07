package kr.co.mathrank.domain.problem.single.read.exception;

import kr.co.mathrank.common.exception.MathRankException;

class SingleProblemReadException extends MathRankException {
	public SingleProblemReadException(int code, String message) {
		super(code, message);
	}
}
