package kr.co.mathrank.domain.auth.dto;

public record JwtOAuthLoginResult(
	String accessToken,
	String refreshToken,
	Boolean isNewUser,
	String userName
) {
	public static JwtOAuthLoginResult from(final JwtLoginResult jwtLoginResult, final Boolean isNewUser) {
		return new JwtOAuthLoginResult(jwtLoginResult.accessToken(), jwtLoginResult.refreshToken(), isNewUser,
			jwtLoginResult.userName());
	}
}
