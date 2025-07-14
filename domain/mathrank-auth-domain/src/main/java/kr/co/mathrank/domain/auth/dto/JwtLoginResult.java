package kr.co.mathrank.domain.auth.dto;

import kr.co.mathrank.common.jwt.JwtResult;

public record JwtLoginResult(
	String accessToken,
	String refreshToken,
	String userName
) {
	public static JwtLoginResult from(final JwtResult result, final String userName) {
		return new JwtLoginResult(result.accessToken(), result.refreshToken(), userName);
	}
}
