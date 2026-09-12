package kr.co.mathrank.domain.auth.client;

import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import kr.co.mathrank.domain.auth.dto.OAuthLoginCommand;
import kr.co.mathrank.domain.auth.entity.OAuthCredentialProfile;
import kr.co.mathrank.domain.auth.entity.OAuthProvider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class NaverOAuthClient implements OAuthClientHandler {
	// 토큰 발급 URI
	private static final String TOKEN_URL = "https://nid.naver.com/oauth2.0/token";
	// 사용자 정보 조회 URI
	private static final String USER_INFO_URL = "https://openapi.naver.com/v1/nid/me";

	private final NaverOAuthProperties naverConfiguration;
	private final OAuthCredentialProfile credentialProfile;
	private final RestClient tokenClient;
	private final RestClient infoClient;

	NaverOAuthClient(final NaverOAuthProperties naverConfiguration,
		final OAuthCredentialProfile credentialProfile) {
		this(naverConfiguration, credentialProfile,
			RestClient.builder().baseUrl(TOKEN_URL).build(),
			RestClient.builder().baseUrl(USER_INFO_URL).build());
	}

	NaverOAuthClient(final NaverOAuthProperties naverConfiguration,
		final OAuthCredentialProfile credentialProfile,
		final RestClient tokenClient,
		final RestClient infoClient) {
		this.naverConfiguration = naverConfiguration;
		this.credentialProfile = credentialProfile;
		this.tokenClient = tokenClient;
		this.infoClient = infoClient;
	}

	@Override
	public MemberInfoResponse getMemberInfo(OAuthLoginCommand command) {
		final AccessTokenResponse token = requireAccessToken(getAccessToken(command), "authorization_code");
		final MemberInfoResponse infoResponse = getUserInfo(token.access_token());

		return new MemberInfoRefreshTokenAdapter(infoResponse.toInfo(), token.refresh_token(), token.token_type());
	}

	private AccessTokenResponse getAccessToken(final OAuthLoginCommand command) {
		final MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
		form.add("grant_type", "authorization_code");
		form.add("client_id", naverConfiguration.getClientId());
		form.add("client_secret", naverConfiguration.getClientSecret());
		form.add("code", command.code());
		form.add("state", command.state());

		return tokenClient.post()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.body(form)
			.retrieve()
			.body(AccessTokenResponse.class);
	}

	private MemberInfoResponse getUserInfo(final String accessToken) {
		return infoClient.get()
			.headers(headers -> headers.setBearerAuth(accessToken.strip()))
			.retrieve()
			.body(NaverMemberInfoResponse.class);
	}

	@Override
	public boolean revoke(final String refreshToken) {
		final AccessTokenResponse token = requireAccessToken(refreshAccessToken(refreshToken), "refresh_token");

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
		final MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
		form.add("grant_type", "refresh_token");
		form.add("client_id", naverConfiguration.getClientId());
		form.add("client_secret", naverConfiguration.getClientSecret());
		form.add("refresh_token", refreshToken);

		return tokenClient.post()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.body(form)
			.retrieve()
			.body(AccessTokenResponse.class);
	}

	private AccessTokenResponse requireAccessToken(final AccessTokenResponse response, final String grantType) {
		if (response == null || response.access_token() == null || response.access_token().isBlank()) {
			final String error = response == null ? "empty_response" : response.error();
			final String description = response == null ? null : response.error_description();
			log.warn("[NaverOAuthClient] access token issuance failed - credentialProfile: {}, grantType: {}, "
				+ "error: {}, errorDescription: {}", credentialProfile, grantType, error, description);
			throw new IllegalStateException("Naver access token issuance failed: " + error);
		}

		log.info("[NaverOAuthClient] access token issued - credentialProfile: {}, grantType: {}, tokenType: {}, "
			+ "accessTokenLength: {}, refreshTokenPresent: {}", credentialProfile, grantType, response.token_type(),
			response.access_token().length(), response.refresh_token() != null && !response.refresh_token().isBlank());
		return response;
	}

	@Override
	public boolean supports(final OAuthProvider provider, final OAuthCredentialProfile credentialProfile) {
		return OAuthProvider.NAVER.equals(provider) && this.credentialProfile == credentialProfile;
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
