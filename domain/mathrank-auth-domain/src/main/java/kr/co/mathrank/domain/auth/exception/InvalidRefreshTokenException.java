package kr.co.mathrank.domain.auth.exception;

public class InvalidRefreshTokenException extends AuthException {
	public InvalidRefreshTokenException() {
		super(1004, "재로그인이 필요합니다. (다른 곳에서 로그인됐거나, 마지막 로그인 날짜가 너무 오래됐습니다)");
	}
}
