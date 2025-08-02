package kr.co.mathrank.domain.problem.single.exception;

import kr.co.mathrank.common.exception.MathRankException;

class SingleProblemException extends MathRankException {
	SingleProblemException(int code, String message) {
		super(code, message);
	}
}
