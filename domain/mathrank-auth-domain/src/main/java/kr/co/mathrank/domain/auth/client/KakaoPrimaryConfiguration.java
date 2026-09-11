package kr.co.mathrank.domain.auth.client;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Configuration
@Getter
@Setter
@ConfigurationProperties("oauth.kakao.primary")
@ConditionalOnProperty(prefix = "oauth.kakao.primary", name = "clientId")
@ToString(exclude = "clientSecret")
class KakaoPrimaryConfiguration implements KakaoOAuthProperties {
	private String grantType = "authorization_code";
	private String clientId;
	private String clientSecret;
	private String redirectUri;
}
