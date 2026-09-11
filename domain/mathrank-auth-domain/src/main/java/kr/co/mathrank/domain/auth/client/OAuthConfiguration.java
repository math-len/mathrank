package kr.co.mathrank.domain.auth.client;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import kr.co.mathrank.domain.auth.entity.OAuthCredentialProfile;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
class OAuthConfiguration {

	@Bean
	@ConditionalOnBean(KakaoConfiguration.class)
	KakaoOAuthClient kakaoOAuthClient(final KakaoConfiguration configuration) {
		log.info("[OAuthClientConfiguration] kakao oauth client registered - configuration: {}", configuration);
		return new KakaoOAuthClient(configuration, OAuthCredentialProfile.LEGACY);
	}

	@Bean
	@ConditionalOnBean(GoogleConfiguration.class)
	GoogleOAuthClient googleOAuthClient(final GoogleConfiguration configuration) {
		log.info("[OAuthClientConfiguration] google oauth client registered - configuration: {}", configuration);
		return new GoogleOAuthClient(configuration, OAuthCredentialProfile.LEGACY);
	}

	@Bean
	@ConditionalOnBean(NaverConfiguration.class)
	NaverOAuthClient naverOAuthClient(final NaverConfiguration configuration) {
		log.info("[OAuthClientConfiguration] naver oauth client registered - configuration: {}", configuration);
		return new NaverOAuthClient(configuration, OAuthCredentialProfile.LEGACY);
	}

	@Bean
	@ConditionalOnBean(KakaoPrimaryConfiguration.class)
	KakaoOAuthClient kakaoPrimaryOAuthClient(final KakaoPrimaryConfiguration configuration) {
		log.info("[OAuthClientConfiguration] primary kakao oauth client registered - configuration: {}", configuration);
		return new KakaoOAuthClient(configuration, OAuthCredentialProfile.PRIMARY);
	}

	@Bean
	@ConditionalOnBean(GooglePrimaryConfiguration.class)
	GoogleOAuthClient googlePrimaryOAuthClient(final GooglePrimaryConfiguration configuration) {
		log.info("[OAuthClientConfiguration] primary google oauth client registered - configuration: {}", configuration);
		return new GoogleOAuthClient(configuration, OAuthCredentialProfile.PRIMARY);
	}

	@Bean
	@ConditionalOnBean(NaverPrimaryConfiguration.class)
	NaverOAuthClient naverPrimaryOAuthClient(final NaverPrimaryConfiguration configuration) {
		log.info("[OAuthClientConfiguration] primary naver oauth client registered - configuration: {}", configuration);
		return new NaverOAuthClient(configuration, OAuthCredentialProfile.PRIMARY);
	}

	@Bean
	@ConditionalOnBean(AppleConfiguration.class)
	AppleOAuthClient appleOAuthClient(final AppleConfiguration configuration) {
		log.info("[OAuthClientConfiguration] apple oauth client registered - configuration: {}", configuration);
		return new AppleOAuthClient(configuration);
	}
}
