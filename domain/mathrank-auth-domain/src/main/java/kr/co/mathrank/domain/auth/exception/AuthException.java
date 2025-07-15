package kr.co.mathrank.domain.auth.exception;

import kr.co.mathrank.common.exception.MathRankException;

public class AuthException extends MathRankException {
	AuthException(int code, String message) {
		super(code, message);
	}
}
