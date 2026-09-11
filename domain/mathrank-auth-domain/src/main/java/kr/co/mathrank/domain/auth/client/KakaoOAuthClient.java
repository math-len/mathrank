package kr.co.mathrank.domain.auth.client;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import kr.co.mathrank.domain.auth.dto.OAuthLoginCommand;
import kr.co.mathrank.domain.auth.entity.OAuthCredentialProfile;
import kr.co.mathrank.domain.auth.entity.OAuthProvider;

class KakaoOAuthClient implements OAuthClientHandler{
	private static final String TOKEN_FORMAT = "Bearer %s";
	private final KakaoOAuthProperties kakaoConfiguration;
	private final OAuthCredentialProfile credentialProfile;

	KakaoOAuthClient(final KakaoOAuthProperties kakaoConfiguration,
		final OAuthCredentialProfile credentialProfile) {
		this.kakaoConfiguration = kakaoConfiguration;
		this.credentialProfile = credentialProfile;
	}
	// https://kauth.kakao.com/oauth/token
	// 토큰 발급 받기
	private final RestClient tokenClient = RestClient.builder()
		.baseUrl("https://kauth.kakao.com/oauth/token")
		.build();

	private final RestClient tokenUnlinkClient = RestClient.builder()
		.baseUrl("https://kapi.kakao.com/v1/user/unlink")
		.build();

	// https://kapi.kakao.com/v2/user/me
	// 사용자 정보 조회
	private final RestClient infoClient = RestClient.builder()
		.baseUrl("https://kapi.kakao.com/v2/user/me")
		.build();

	@Override
	public MemberInfoResponse getMemberInfo(OAuthLoginCommand command) {
		final AccessTokenResponse token = getAccessToken(command.code());
		final MemberInfoResponse infoResponse = getKakaoInfoResponse(token.access_token());

		return new MemberInfoRefreshTokenAdapter(infoResponse.toInfo(), token.refresh_token(), token.token_type());
	}

	public AccessTokenResponse getAccessToken(final String code) {
		return tokenClient.post()
			.uri(uriBuilder -> uriBuilder
				.queryParam("grant_type", kakaoConfiguration.getGrantType())
				.queryParam("client_id", kakaoConfiguration.getClientId())
				.queryParam("redirect_uri", kakaoConfiguration.getRedirectUri())
				.queryParam("client_secret", kakaoConfiguration.getClientSecret())
				.queryParam("code", code)
				.build())
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.retrieve()
			.body(AccessTokenResponse.class);
	}

	public KakaoMemberInfoResponse getKakaoInfoResponse(final String accessToken) {
		return infoClient.post()
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header(HttpHeaders.AUTHORIZATION, TOKEN_FORMAT.formatted(accessToken))
			.retrieve()
			.body(KakaoMemberInfoResponse.class);
	}

	@Override
	public boolean supports(final OAuthProvider provider, final OAuthCredentialProfile credentialProfile) {
		return provider == OAuthProvider.KAKAO && this.credentialProfile == credentialProfile;
	}

	@Override
	public boolean revoke(final String refreshToken) {
		final AccessTokenResponse token = refreshByRefreshToken(refreshToken);
		return revokeByAccessToken(token.access_token());
	}

	private boolean revokeByAccessToken(final String accessToken) {
		return tokenUnlinkClient.post()
			.header(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(accessToken))
			.retrieve()
			.body(KakaoRevokeResponse.class)
			.succeed();
	}

	private AccessTokenResponse refreshByRefreshToken(final String refreshToken) {
		return tokenClient.post()
			.uri(uriBuilder -> uriBuilder
				.queryParam("grant_type", "refresh_token")
				.queryParam("client_id", kakaoConfiguration.getClientId())
				.queryParam("refresh_token", refreshToken)
				.queryParam("client_secret", kakaoConfiguration.getClientSecret())
				.build()
			)
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.retrieve()
			.body(AccessTokenResponse.class);
	}

	private record KakaoRevokeResponse(
		String id
	) {
		boolean succeed() {
			return id != null;
		}
	}
}
