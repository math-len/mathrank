package kr.co.mathrank.domain.contents.exception;

import kr.co.mathrank.common.exception.MathRankException;

class ContentException extends MathRankException {
	public ContentException(int code, String message) {
		super(code, message);
	}
}
