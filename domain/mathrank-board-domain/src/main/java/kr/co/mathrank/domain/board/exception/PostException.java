package kr.co.mathrank.domain.board.exception;

import kr.co.mathrank.common.exception.MathRankException;

public class PostException extends MathRankException {
	public PostException(int code, String message) {
		super(code, message);
	}
}
