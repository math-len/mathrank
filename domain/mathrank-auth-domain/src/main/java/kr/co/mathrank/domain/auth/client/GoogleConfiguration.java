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
@ConfigurationProperties("oauth.google")
@ConditionalOnProperty(prefix = "oauth.google", name = "clientId")
@ToString(exclude = "clientSecret")
class GoogleConfiguration {
	private String clientId;
	private String clientSecret;
	private String redirectUri;
}

