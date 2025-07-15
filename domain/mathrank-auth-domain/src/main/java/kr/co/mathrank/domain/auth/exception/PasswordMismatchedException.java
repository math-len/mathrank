package kr.co.mathrank.domain.auth.exception;

public class PasswordMismatchedException extends AuthException {
	private static final String FORMAT = "비밀번호 오류, 남은 횟수: %s";
	public PasswordMismatchedException(final int remainTryCount) {
		super(1002, FORMAT.formatted(remainTryCount));
	}
}
