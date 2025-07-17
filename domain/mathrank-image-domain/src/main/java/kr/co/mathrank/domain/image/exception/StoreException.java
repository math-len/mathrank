package kr.co.mathrank.domain.image.exception;

import kr.co.mathrank.common.exception.HttpMathRankException;

public class StoreException extends HttpMathRankException {
	private static final int HTTP_STATUS_CODE = 504; // 일시적 사용 불가
	private static final int EXCEPTION_CODE = 4002; // 파일 저장 불가 코드

	public StoreException(String message) {
		super(HTTP_STATUS_CODE, EXCEPTION_CODE, message);
	}
}
