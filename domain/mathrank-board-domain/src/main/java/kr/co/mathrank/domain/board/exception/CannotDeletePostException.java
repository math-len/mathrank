package kr.co.mathrank.domain.board.exception;

public class CannotDeletePostException extends PostException {
	public CannotDeletePostException() {
		super(9002, "게시글을 삭제할 수 있는 권한 부재");
	}
}
