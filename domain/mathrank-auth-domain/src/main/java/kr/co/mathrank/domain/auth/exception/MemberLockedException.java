package kr.co.mathrank.domain.auth.exception;

import java.time.Duration;

public class MemberLockedException extends AuthException {
	private static final String FORMAT = "잠긴 계정입니다. 남은 잠금 시간: %s(분)";
	public MemberLockedException(final Duration duration) {
		super(1003, FORMAT.formatted(duration.toMinutes()));
	}
}
