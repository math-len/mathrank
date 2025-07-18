package kr.co.mathrank.common.exception;

import lombok.Getter;

@Getter
public class HttpMathRankException extends MathRankException {
	private final int httpStatusCode;

	public HttpMathRankException(int httpStatusCode, int code, String message) {
		super(code, message);
		this.httpStatusCode = httpStatusCode;
	}
}
