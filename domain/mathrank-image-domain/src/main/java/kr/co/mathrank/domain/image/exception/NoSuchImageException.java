package kr.co.mathrank.domain.image.exception;

import kr.co.mathrank.common.exception.HttpMathRankException;

public class NoSuchImageException extends HttpMathRankException {
	private static final int HTTP_STATUS_CODE = 404; // 찾을 수 없음
	private static final int EXCEPTION_CODE = 4001;

	public NoSuchImageException(String message) {
		super(HTTP_STATUS_CODE, EXCEPTION_CODE, message);
	}
}
