package kr.co.mathrank.domain.point.exception;

import kr.co.mathrank.common.exception.MathRankException;

class PointException extends MathRankException {
	public PointException(int code, String message) {
		super(code, message);
	}
}
