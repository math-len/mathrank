package kr.co.mathrank.domain.board.exception;

public class CannotUpdatePostException extends PostException {
	public CannotUpdatePostException() {
		super(9003, "게시글을 수정할 수 있는 권한 부재");
	}
}
