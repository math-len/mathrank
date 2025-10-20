package kr.co.mathrank.domain.auth.client;

record AccessTokenResponse(
	String token_type,
	String access_token
) {
}
