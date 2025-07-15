package kr.co.mathrank.domain.auth.exception;

public class CannotFoundMemberException extends AuthException{
	public CannotFoundMemberException() {
		super(1001, "사용자를 찾을 수 없습니다.");
	}
}
