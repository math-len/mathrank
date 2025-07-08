package kr.co.mathrank.common.jwt;

public record JwtResult(
	String accessToken,
	String refreshToken
) {
}
