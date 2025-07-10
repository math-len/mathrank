package kr.co.mathrank.app.api.common.authentication;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import kr.co.mathrank.common.jwt.JwtUtil;

@Configuration
public class AuthenticationConfiguration {
	@Bean
	public OncePerRequestFilter jwtAuthenticationFilter(final JwtUtil jwtUtil) {
		return new JwtAuthenticationFilter(jwtUtil);
	}

	@Bean
	public WebMvcConfigurer authenticationWebMvcConfigurer() {
		return new WebAuthorizationConfig();
	}

	private static class WebAuthorizationConfig implements WebMvcConfigurer {
		@Override
		public void addInterceptors(InterceptorRegistry registry) {
			registry.addInterceptor(new AuthorizationInterceptor())
				.addPathPatterns("/api/**");
		}

		@Override
		public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
			resolvers.add(new LoginIdMethodArgumentResolver());
		}
	}
}
