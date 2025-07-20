package kr.co.mathrank.domain.auth.exception;

import kr.co.mathrank.common.exception.MathRankException;

public class InvalidOAuthLoginException extends MathRankException {
	private static final int ERROR_CODE = 1006;
	public InvalidOAuthLoginException(String message) {
		super(ERROR_CODE, message);
	}
}
