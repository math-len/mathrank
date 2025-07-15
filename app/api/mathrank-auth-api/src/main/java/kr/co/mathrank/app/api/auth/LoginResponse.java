package kr.co.mathrank.app.api.auth;

public record LoginResponse(
	String accessToken,
	String userName
) {
}
