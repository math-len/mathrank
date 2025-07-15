package kr.co.mathrank.domain.auth.exception;

public class MemberLockedException extends AuthException {
	public MemberLockedException() {
		super(1003, "잠긴 계정 입니다.");
	}
}
