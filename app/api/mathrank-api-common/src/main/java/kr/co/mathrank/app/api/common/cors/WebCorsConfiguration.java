package kr.co.mathrank.app.api.common.cors;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
class WebCorsConfiguration {
	@Bean
	@Primary
	@ConditionalOnProperty(name = "cors.mode", havingValue = "develop")
	CorsConfigurationSource corsDevelopConfigurationSource() {
		log.info("[WebCorsConfiguration]: set up with [develop] env, allowed origin: {}", "*");
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.addAllowedOriginPattern("*");
		corsConfiguration.addAllowedMethod("*");
		corsConfiguration.setAllowedHeaders(List.of("*"));
		corsConfiguration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfiguration);
		return source;
	}

	@Bean
	@Primary
	@ConditionalOnProperty(name = "cors.mode", havingValue = "deploy", matchIfMissing = true)
	CorsConfigurationSource corsDeployConfiguration(
		@Value("${cors.allowed.origin}") String origin
	) {
		log.info("[WebCorsConfiguration]: set up with [deploy] env, allowed origin: {}", origin);
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.addAllowedOriginPattern(origin);
		corsConfiguration.addAllowedMethod("*");
		corsConfiguration.setAllowedHeaders(List.of("*"));
		corsConfiguration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfiguration);
		return source;
	}

	@Bean
	public CorsFilter corsFilter(CorsConfigurationSource source) {
		return new CorsFilter(source);
	}
}
