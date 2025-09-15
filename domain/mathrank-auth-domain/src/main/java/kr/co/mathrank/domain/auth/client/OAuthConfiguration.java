package kr.co.mathrank.domain.auth.client;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
class OAuthConfiguration {

	@Bean
	@ConditionalOnBean(KakaoConfiguration.class)
	KakaoOAuthClient kakaoOAuthClient(final KakaoConfiguration configuration) {
		log.info("[OAuthClientConfiguration] kakao oauth client registered - configuration: {}", configuration);
		return new KakaoOAuthClient(configuration);
	}

	@Bean
	@ConditionalOnBean(GoogleConfiguration.class)
	GoogleOAuthClient googleOAuthClient(final GoogleConfiguration configuration) {
		log.info("[OAuthClientConfiguration] google oauth client registered - configuration: {}", configuration);
		return new GoogleOAuthClient(configuration);
	}

	@Bean
	@ConditionalOnBean(NaverConfiguration.class)
	NaverOAuthClient naverOAuthClient(final NaverConfiguration configuration) {
		log.info("[OAuthClientConfiguration] naver oauth client registered - configuration: {}", configuration);
		return new NaverOAuthClient(configuration);
	}
}
