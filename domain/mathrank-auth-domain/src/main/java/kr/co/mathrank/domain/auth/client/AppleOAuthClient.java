package kr.co.mathrank.domain.auth.client;

import java.security.KeyFactory;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.ParseException;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import kr.co.mathrank.domain.auth.dto.OAuthLoginCommand;
import kr.co.mathrank.domain.auth.entity.OAuthCredentialProfile;
import kr.co.mathrank.domain.auth.entity.OAuthProvider;
import kr.co.mathrank.domain.auth.exception.InvalidOAuthLoginException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class AppleOAuthClient implements OAuthClientHandler {
	private final AppleConfiguration appleConfiguration;

	// 토큰 발급 URL
	// https://developer.apple.com/documentation/sign_in_with_apple/generate_and_validate_tokens
	private static final String TOKEN_URL = "https://appleid.apple.com/auth/token";
	// 토큰 폐기 URL
	private static final String TOKEN_REVOKE_URL = "https://appleid.apple.com/auth/revoke";

	private final RestClient tokenClient = RestClient.builder()
		.baseUrl(TOKEN_URL)
		.build();

	private final RestClient revokeClient = RestClient.builder()
		.baseUrl(TOKEN_REVOKE_URL)
		.build();

	@Override
	public MemberInfoResponse getMemberInfo(OAuthLoginCommand command) {
		final AppleTokenResponse token = getAccessToken(command);
		final MemberInfoResponse infoResponse = parseIdToken(token.id_token());

		return new MemberInfoRefreshTokenAdapter(infoResponse.toInfo(), token.refresh_token(), token.token_type());
	}

	private AppleTokenResponse getAccessToken(final OAuthLoginCommand command) {
		final String clientSecret = generateClientSecret();

		return tokenClient.post()
			.uri(uriBuilder -> uriBuilder
				.queryParam("client_id", appleConfiguration.getClientId())
				.queryParam("client_secret", clientSecret)
				.queryParam("code", command.code())
				.queryParam("grant_type", "authorization_code")
				.build())
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.retrieve()
			.body(AppleTokenResponse.class);
	}

	// Apple은 별도의 사용자 정보 조회 API가 없으며, id_token(JWT)에서 사용자 정보를 추출한다.
	private AppleMemberInfoResponse parseIdToken(final String idToken) {
		try {
			final SignedJWT signedJWT = SignedJWT.parse(idToken);
			final JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

			return new AppleMemberInfoResponse(
				claims.getSubject(),
				claims.getStringClaim("email")
			);
		} catch (ParseException e) {
			throw new InvalidOAuthLoginException("애플 서버로부터 사용할 수 없는 메시지를 받았습니다.");
		}
	}

	// Apple OAuth의 client_secret은 ES256으로 서명된 JWT이다.
	// https://developer.apple.com/documentation/sign_in_with_apple/generate_and_validate_tokens
	private String generateClientSecret() {
		try {
			final ECPrivateKey ecPrivateKey = loadPrivateKey(appleConfiguration.getPrivateKey());
			final Instant now = Instant.now();

			final JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES256)
				.keyID(appleConfiguration.getKeyId())
				.build();

			final JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
				.issuer(appleConfiguration.getTeamId())
				.issueTime(Date.from(now))
				.expirationTime(Date.from(now.plusSeconds(300)))
				.audience("https://appleid.apple.com")
				.subject(appleConfiguration.getClientId())
				.build();

			final SignedJWT signedJWT = new SignedJWT(header, claimsSet);
			signedJWT.sign(new ECDSASigner(ecPrivateKey));

			return signedJWT.serialize();
		} catch (JOSEException e) {
			throw new InvalidOAuthLoginException("애플 client_secret 생성에 실패했습니다.");
		}
	}

	private ECPrivateKey loadPrivateKey(final String privateKey) {
		try {
			final String cleanedKey = privateKey
				.replace("-----BEGIN PRIVATE KEY-----", "")
				.replace("-----END PRIVATE KEY-----", "")
				.replaceAll("\\s", "");

			final byte[] keyBytes = Base64.getDecoder().decode(cleanedKey);
			final PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
			final KeyFactory keyFactory = KeyFactory.getInstance("EC");

			return (ECPrivateKey)keyFactory.generatePrivate(keySpec);
		} catch (Exception e) {
			throw new InvalidOAuthLoginException("애플 개인 키 로딩에 실패했습니다.");
		}
	}

	@Override
	public boolean supports(final OAuthProvider provider, final OAuthCredentialProfile credentialProfile) {
		return OAuthProvider.APPLE.equals(provider) && credentialProfile == OAuthCredentialProfile.LEGACY;
	}

	// 애플은 refreshToken으로 직접 토큰 폐기 가능
	// https://developer.apple.com/documentation/sign_in_with_apple/revoke_tokens
	@Override
	public boolean revoke(String refreshToken) {
		final String clientSecret = generateClientSecret();

		return revokeClient.post()
			.uri(uriBuilder -> uriBuilder
				.queryParam("client_id", appleConfiguration.getClientId())
				.queryParam("client_secret", clientSecret)
				.queryParam("token", refreshToken)
				.queryParam("token_type_hint", "refresh_token")
				.build())
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.retrieve()
			.toBodilessEntity()
			.getStatusCode().is2xxSuccessful();
	}
}
