package kr.co.mathrank.domain.board.exception;

public class CannotDeleteCommentException extends PostException {
	public CannotDeleteCommentException() {
		super(9005, "댓글을 삭제할 수 없습니다.");
	}
}
