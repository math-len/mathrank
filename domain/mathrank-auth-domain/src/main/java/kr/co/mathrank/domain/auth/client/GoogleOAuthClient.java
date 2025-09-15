package kr.co.mathrank.domain.auth.client;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import kr.co.mathrank.domain.auth.dto.OAuthLoginCommand;
import kr.co.mathrank.domain.auth.entity.OAuthProvider;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class GoogleOAuthClient implements OAuthClientHandler {
	private static final String TOKEN_FORMAT = "Bearer %s";
	private final GoogleConfiguration googleConfiguration;

	// 토큰 URL
	private static final String TOKEN_URL = "https://oauth2.googleapis.com/token";
	// 사용자 정보 조회 URL
	private static final String INFO_URL = "https://www.googleapis.com/oauth2/v2/userinfo";

	private final RestClient tokenClient = RestClient.builder()
		.baseUrl(TOKEN_URL)
		.build();

	private final RestClient infoClient = RestClient.builder()
		.baseUrl(INFO_URL)
		.build();

	@Override
	public MemberInfoResponse getMemberInfo(OAuthLoginCommand command) {
		return getInfo(getAccessToken(command).access_token());
	}

	private GoogleInfoResponse getInfo(final String accessToken) {
		return infoClient.post()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
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
	public boolean supports(OAuthProvider provider) {
		return false;
	}
}
