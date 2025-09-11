package kr.co.mathrank.client.internal.member;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import kr.co.mathrank.client.exception.aspect.Client;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Client
public class MemberClient {
	private static final String URL_FORMAT = "%s:%s";

	private final MemberClientProperties properties;
	private final RestClient restClient;

	MemberClient(final MemberClientProperties properties) {
		this.properties = properties;
		this.restClient = RestClient.builder()
			.requestFactory(configureTimeoutConfiguration(properties))
			.baseUrl(getUrlFormat(properties.getHost(), properties.getPort()))
			.build();
	}

	private static ClientHttpRequestFactory configureTimeoutConfiguration(final MemberClientProperties properties) {
		final SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
		clientHttpRequestFactory.setConnectTimeout(Duration.ofSeconds(properties.getConnectionTimeoutSeconds()));
		clientHttpRequestFactory.setReadTimeout(Duration.ofSeconds(properties.getReadTimeoutSeconds()));
		return clientHttpRequestFactory;
	}

	public MemberInfo getMemberInfo(final Long memberId) {
		return restClient.get()
			.uri(uriBuilder -> uriBuilder
				.path(properties.uri)
				.queryParam("memberId", memberId)
				.build())
			.retrieve()
			.body(MemberInfo.class);
	}

	private String getUrlFormat(final String host, final Integer port) {
		return URL_FORMAT.formatted(host, port);
	};

	@Getter
	@Configuration
	@ConfigurationProperties("client.member")
	@NoArgsConstructor
	@Setter
	static class MemberClientProperties {
		private String host = "http://localhost";
		private Integer port = 8080;
		private String uri = "/api/inner/v1/member/info";
		private Integer connectionTimeoutSeconds;
		private Integer readTimeoutSeconds;
	}
}
