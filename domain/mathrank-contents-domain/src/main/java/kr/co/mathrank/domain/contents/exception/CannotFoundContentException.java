package kr.co.mathrank.domain.contents.exception;

public class CannotFoundContentException extends ContentException {
	public CannotFoundContentException() {
		super(11002, "찾을 수 없는 자료입니다.");
	}
}
