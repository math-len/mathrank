package kr.co.mathrank.domain.rank.exception;

public class CannotCalculateRankException extends RankException {
	public CannotCalculateRankException(String message) {
		super(8003, message);
	}
}
