package kr.co.mathrank.app.api.common.exception;

public record ApiExceptionBody(
	int code,
	String message
) {
	public static ApiExceptionBody of(final int code, final String message) {
		return new ApiExceptionBody(code, message);
	}
}
