package kr.co.mathrank.domain.auth.client;

record AccessTokenResponse(
	String token_type,
	String access_token,
	String refresh_token,
	String expires_in,
	String error,
	String error_description
) {
}
