package kr.co.mathrank.domain.auth.client;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import kr.co.mathrank.domain.auth.dto.OAuthLoginCommand;
import kr.co.mathrank.domain.auth.entity.OAuthProvider;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class NaverOAuthClient implements OAuthClientHandler {
	private static final String TOKEN_FORMAT = "Bearer %s";
	// 토큰 발급 URI
	private static final String TOKEN_URL = "https://nid.naver.com/oauth2.0/token";
	// 사용자 정보 조회 URI
	private static final String USER_INFO_URL = "https://openapi.naver.com/v1/nid/me";

	private final NaverConfiguration naverConfiguration;

	private final RestClient tokenClient = RestClient.builder()
		.baseUrl(TOKEN_URL)
		.build();

	private final RestClient infoClient = RestClient.builder()
		.baseUrl(USER_INFO_URL)
		.build();

	@Override
	public MemberInfoResponse getMemberInfo(OAuthLoginCommand command) {
		return getUserInfo(getAccessToken(command).access_token());
	}

	private AccessTokenResponse getAccessToken(final OAuthLoginCommand command) {
		return tokenClient.post()
			.uri(uriBuilder -> uriBuilder
				.queryParam("grant_type", "authorization_code")
				.queryParam("client_id", naverConfiguration.getClientId())
				.queryParam("client_secret", naverConfiguration.getClientSecret())
				.queryParam("code", command.code())
				.queryParam("state", command.state())
				.build())
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.retrieve()
			.body(AccessTokenResponse.class);
	}

	private MemberInfoResponse getUserInfo(final String accessToken) {
		return infoClient.post()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HttpHeaders.AUTHORIZATION, TOKEN_FORMAT.formatted(accessToken))
			.retrieve()
			.body(KakaoMemberInfoResponse.class);
	}

	@Override
	public boolean supports(OAuthProvider provider) {
		return OAuthProvider.NAVER.equals(provider);
	}
}
