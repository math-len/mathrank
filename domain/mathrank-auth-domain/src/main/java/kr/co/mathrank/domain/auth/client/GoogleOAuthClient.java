package kr.co.mathrank.domain.auth.client;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import kr.co.mathrank.domain.auth.dto.OAuthLoginCommand;
import kr.co.mathrank.domain.auth.entity.OAuthCredentialProfile;
import kr.co.mathrank.domain.auth.entity.OAuthProvider;

class GoogleOAuthClient implements OAuthClientHandler {
	private static final String TOKEN_FORMAT = "Bearer %s";
	private final GoogleOAuthProperties googleConfiguration;
	private final OAuthCredentialProfile credentialProfile;

	GoogleOAuthClient(final GoogleOAuthProperties googleConfiguration,
		final OAuthCredentialProfile credentialProfile) {
		this.googleConfiguration = googleConfiguration;
		this.credentialProfile = credentialProfile;
	}

	// 토큰 URL
	private static final String TOKEN_URL = "https://oauth2.googleapis.com/token";
	// 토큰 폐기 URL
	private static final String TOKEN_REVOKE_URL = "https://oauth2.googleapis.com/revoke";
	// 사용자 정보 조회 URL
	private static final String INFO_URL = "https://www.googleapis.com/oauth2/v2/userinfo";


	private final RestClient tokenClient = RestClient.builder()
		.baseUrl(TOKEN_URL)
		.build();

	private final RestClient infoClient = RestClient.builder()
		.baseUrl(INFO_URL)
		.build();

	private final RestClient revokeClient = RestClient.builder()
		.baseUrl(TOKEN_REVOKE_URL)
		.build();

	@Override
	public MemberInfoResponse getMemberInfo(OAuthLoginCommand command) {
		final AccessTokenResponse token = getAccessToken(command);
		final MemberInfoResponse infoResponse = getInfo(token.access_token());

		return new MemberInfoRefreshTokenAdapter(infoResponse.toInfo(), token.refresh_token(), token.token_type());
	}

	private GoogleInfoResponse getInfo(final String accessToken) {
		return infoClient.get()
			.header(HttpHeaders.AUTHORIZATION, TOKEN_FORMAT.formatted(accessToken))
			.retrieve()
			.body(GoogleInfoResponse.class);
	}

	private AccessTokenResponse getAccessToken(final OAuthLoginCommand command) {
		return tokenClient.post()
			.uri(uriBuilder -> uriBuilder
				.queryParam("client_id", googleConfiguration.getClientId())
				.queryParam("client_secret", googleConfiguration.getClientSecret())
				.queryParam("code", command.code())
				.queryParam("grant_type", "authorization_code")
				.queryParam("redirect_uri", googleConfiguration.getRedirectUri())
				.build())
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.retrieve()
			.body(AccessTokenResponse.class);
	}

	@Override
	public boolean supports(final OAuthProvider provider, final OAuthCredentialProfile credentialProfile) {
		return OAuthProvider.GOOGLE.equals(provider) && this.credentialProfile == credentialProfile;
	}

	// 구글은 refreshToken 만으로도 삭제 가능
	// https://developers.google.com/identity/protocols/oauth2/web-server?hl=ko
	@Override
	public boolean revoke(String refreshToken) {
		return revokeClient.post()
			.uri(uriBuilder -> uriBuilder
				.queryParam("token", refreshToken)
				.build()
			)
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.retrieve()
			.toBodilessEntity()
			.getStatusCode().is2xxSuccessful();
	}
}
