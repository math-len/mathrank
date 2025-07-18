package kr.co.mathrank.domain.image.exception;

import kr.co.mathrank.common.exception.HttpMathRankException;

public class NotSupportedMediaException extends HttpMathRankException {
	private static final int HTTP_STATUS_CODE = 404; // NOT FOUND 리소스
	private static final int EXCEPTION_CODE = 4003; // 지원하지 않는 미디어 타입

	public NotSupportedMediaException(String message) {
		super(HTTP_STATUS_CODE, EXCEPTION_CODE, message);
	}
}
