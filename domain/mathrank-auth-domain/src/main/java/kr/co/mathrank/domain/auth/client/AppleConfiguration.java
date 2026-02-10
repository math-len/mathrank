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
@ConfigurationProperties("oauth.apple")
@ConditionalOnProperty(prefix = "oauth.apple", name = "clientId")
@ToString(exclude = "privateKey")
class AppleConfiguration {
	private String clientId;
	private String teamId;
	private String keyId;
	private String privateKey;
}
