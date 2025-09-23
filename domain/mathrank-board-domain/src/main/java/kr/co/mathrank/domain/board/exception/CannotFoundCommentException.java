package kr.co.mathrank.domain.board.exception;

public class CannotFoundCommentException extends PostException {
	public CannotFoundCommentException() {
		super(9003, "댓글을 찾을 수 없음");
	}
}
