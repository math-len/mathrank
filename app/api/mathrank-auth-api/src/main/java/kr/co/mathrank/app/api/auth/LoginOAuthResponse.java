package kr.co.mathrank.app.api.auth;

public record LoginOAuthResponse(
	String accessToken,
	String userName,
	Boolean isNewUser
) {
}
