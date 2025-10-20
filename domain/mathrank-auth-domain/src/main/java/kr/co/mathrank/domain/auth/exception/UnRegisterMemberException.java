package kr.co.mathrank.domain.auth.exception;

public class UnRegisterMemberException extends AuthException {
	public UnRegisterMemberException(String message) {
		super(1007, message);
	}
}
