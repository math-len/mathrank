package kr.co.mathrank.common.exception;

import lombok.Getter;

@Getter
public class MathRankException extends RuntimeException {
	private final int code;

	public MathRankException(final int code, final String message) {
		super(message);
		this.code = code;
	}
}
