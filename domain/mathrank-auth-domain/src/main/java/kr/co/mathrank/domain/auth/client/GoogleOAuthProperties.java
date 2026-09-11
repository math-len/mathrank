package kr.co.mathrank.domain.auth.client;

interface GoogleOAuthProperties {
	String getClientId();
	String getClientSecret();
	String getRedirectUri();
}
