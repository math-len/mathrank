package kr.co.mathrank.app.api.auth;

public class Responses {
	public static record LoginOAuthResponse(
		String accessToken,
		String userName,
		Boolean isNewUser
	) {
	}

	public static record LoginResponse(
		String accessToken,
		String userName
	) {
	}
}
