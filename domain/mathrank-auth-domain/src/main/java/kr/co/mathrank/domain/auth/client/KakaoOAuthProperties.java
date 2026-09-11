package kr.co.mathrank.domain.auth.client;

interface KakaoOAuthProperties {
	String getGrantType();
	String getClientId();
	String getClientSecret();
	String getRedirectUri();
}
