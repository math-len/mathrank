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
		final AccessTokenResponse token = getAccessToken(command);
		final MemberInfoResponse infoResponse = getUserInfo(token.access_token());
		
		return new MemberInfoRefreshTokenAdapter(infoResponse.toInfo(), token.refresh_token(), token.token_type());
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
			.body(NaverMemberInfoResponse.class);
	}

	@Override
	public boolean revoke(final String refreshToken) {
		final AccessTokenResponse token = refreshAccessToken(refreshToken);

		return tokenClient.post()
			.uri(uriBuilder -> uriBuilder
				.queryParam("client_id", naverConfiguration.getClientId())
				.queryParam("client_secret", naverConfiguration.getClientSecret())
				.queryParam("access_token", "{access_token}")
				.queryParam("grant_type", "delete")
				.queryParam("service_provider", "NAVER")
				.build(token.access_token()))
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.retrieve()
			.body(TokenRevokeResponse.class)
			.succeeded();
	}

	private AccessTokenResponse refreshAccessToken(String refreshToken) {
		return tokenClient.post()
			.uri(uriBuilder -> uriBuilder
				.queryParam("grant_type", "refresh_token")
				.queryParam("client_id", naverConfiguration.getClientId())
				.queryParam("client_secret", naverConfiguration.getClientSecret())
				.queryParam("refresh_token", refreshToken)
				.build())
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.retrieve()
			.body(AccessTokenResponse.class);
	}

	@Override
	public boolean supports(OAuthProvider provider) {
		return OAuthProvider.NAVER.equals(provider);
	}

	record TokenRevokeResponse(
		String result
	) {
		boolean succeeded() {
			if (result == null) {
				return false;
			}

			return result.equals("success");
		}
	}
}
