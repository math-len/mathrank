package kr.co.mathrank.domain.auth.exception;

public class AlreadyExistLoginIdException extends AuthException {
	public AlreadyExistLoginIdException() {
		super(1005, "이미 존재하는 아이디입니다.");
	}
}
