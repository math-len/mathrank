package kr.co.mathrank.domain.board.exception;

public class CannotFoundPostException extends PostException {
	public CannotFoundPostException() {
		super(9001, "게시글을 찾을 수 없음");
	}
}
