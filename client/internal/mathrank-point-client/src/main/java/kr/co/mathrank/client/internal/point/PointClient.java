package kr.co.mathrank.client.internal.point;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestClient;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.client.config.TimeoutConfiguredClient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
public class PointClient extends TimeoutConfiguredClient {
	private static final String URL_FORMAT = "%s:%s";

	private final PointClientProperties properties;
	private final RestClient restClient;

	PointClient(final PointClientProperties properties) {
		this.properties = properties;
		this.restClient = RestClient.builder()
			.requestFactory(configureTimeoutConfiguration(
				Duration.ofSeconds(properties.getConnectionTimeoutSeconds()),
				Duration.ofSeconds(properties.getReadTimeoutSeconds()))
			)
			.baseUrl(getUrlFormat(properties.getHost(), properties.getPort()))
			.build();
	}

	public PointInfo getRemainPoint(final Long memberId) {
		return restClient.get()
			.uri(uriBuilder -> uriBuilder
				.path(properties.getUri())
				.queryParam("memberId", memberId)
				.build())
			.retrieve()
			.body(PointInfo.class);
	}

	private String getUrlFormat(final String host, final Integer port) {
		return URL_FORMAT.formatted(host, port);
	};

	@Getter
	@Configuration
	@ConfigurationProperties("client.point")
	@NoArgsConstructor
	@Validated
	@Setter
	static class PointClientProperties {
		@NotNull
		private String host = "http://localhost";
		@NotNull
		private Integer port = 8080;
		@NotNull
		private String uri = "/api/inner/v1/point";
		@NotNull
		private Integer connectionTimeoutSeconds;
		@NotNull
		private Integer readTimeoutSeconds;
	}
}
