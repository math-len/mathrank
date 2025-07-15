package kr.co.mathrank.app.api.auth.cookie;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
class RefreshTokenCookieConfiguration {
	@Bean
	@ConditionalOnProperty(name = "cookie.mode", havingValue = "deploy", matchIfMissing = true)
	RefreshTokenCookieManager deployEnvCookieManager() {
		log.info("[RefreshTokenCookieConfiguration]: Setting up cookie manager for [deploy] environment");
		return new DeployCookieManager();
	}

	@Bean
	@ConditionalOnProperty(name = "cookie.mode", havingValue = "develop")
	RefreshTokenCookieManager developEnvCookieManager() {
		log.info("[RefreshTokenCookieConfiguration]: Setting up cookie manager for [develop] environment");
		return new DevelopCookieManager();
	}
}
