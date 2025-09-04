package kr.co.mathrank.domain.rank.exception;

import kr.co.mathrank.common.exception.MathRankException;

public class RankException extends MathRankException {
	public RankException(int code, String message) {
		super(code, message);
	}
}
