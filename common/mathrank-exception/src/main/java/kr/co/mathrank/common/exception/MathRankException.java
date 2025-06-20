package kr.co.mathrank.common.exception;

import lombok.Getter;

@Getter
public class MathRankException extends RuntimeException {

	private ExceptionMessage exceptionMessage;

	public MathRankException(ExceptionMessage message) {
		super(message.getMessage());
		this.exceptionMessage = message;
	}

	public MathRankException(ExceptionMessage message, Throwable cause) {
		super(message.getMessage(), cause);
		this.exceptionMessage = message;
	}

	public MathRankException(String message) {
		super(message);
		this.exceptionMessage = () -> message;
	}
}
