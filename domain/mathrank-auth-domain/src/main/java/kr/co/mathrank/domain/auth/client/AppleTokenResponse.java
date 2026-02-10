package kr.co.mathrank.domain.auth.client;

record AppleTokenResponse(
	String token_type,
	String access_token,
	String refresh_token,
	String id_token
) {
}
