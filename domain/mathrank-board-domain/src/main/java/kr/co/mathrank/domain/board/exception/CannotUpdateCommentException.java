package kr.co.mathrank.domain.board.exception;

public class CannotUpdateCommentException extends PostException {
	public CannotUpdateCommentException() {
		super(9004, "댓글은 본인만 수정할 수 있습니다.");
	}
}
