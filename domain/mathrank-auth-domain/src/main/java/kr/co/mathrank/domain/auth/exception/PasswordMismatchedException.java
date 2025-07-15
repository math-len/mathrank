package kr.co.mathrank.domain.auth.exception;

public class PasswordMismatchedException extends AuthException {
	public PasswordMismatchedException() {
		super(1002, "비밀번호 오류");
	}
}
