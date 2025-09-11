package kr.co.mathrank.client.internal.problem;

import java.time.Duration;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestClient;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.client.exception.aspect.Client;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Validated
@Client
public class ProblemClient {
	private static final String URL_FORMAT = "%s:%s";
	private final RestClient problemClient;

	ProblemClient(final ProblemClientProperties properties) {
		final String url = URL_FORMAT.formatted(properties.getHost(), properties.getPort());
		log.info("[ProblemClient.new] initialized with url: {}", url);

		this.problemClient = RestClient.builder()
			.requestFactory(configureTimeoutConfiguration(properties))
			.baseUrl(url)
			.build();
	}

	private static ClientHttpRequestFactory configureTimeoutConfiguration(final ProblemClientProperties properties) {
		final SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
		clientHttpRequestFactory.setConnectTimeout(Duration.ofSeconds(properties.getConnnectTimeoutSeconds()));
		clientHttpRequestFactory.setReadTimeout(Duration.ofSeconds(properties.getReadTimeoutSeconds()));
		return clientHttpRequestFactory;
	}

	public SolveResult matchAnswer(@NotNull final Long problemId, @NotNull final List<String> answers) {
		return problemClient.get()
			.uri(uri -> uri
				.path("/api/inner/v1/problem/solve")
				.queryParam("problemId", problemId)
				.queryParam("answers", answers)
				.build())
			.retrieve()
			.body(SolveResult.class);
	}

	public ProblemQueryResult fetchProblemInfo(final Long problemId) {
		return problemClient.get()
			.uri(uriBuilder -> uriBuilder
				.path("/api/inner/v1/problem/{problemId}")
				.build(problemId))
			.retrieve()
			.body(ProblemQueryResult.class);
	}

	@Getter
	@Configuration
	@ConfigurationProperties("client.problem")
	@NoArgsConstructor
	@Setter
	@Validated
	static class ProblemClientProperties {
		@NotNull
		private String host;
		@NotNull
		private Integer port;
		@NotNull
		private Integer connnectTimeoutSeconds;
		@NotNull
		private Integer readTimeoutSeconds;
	}
}
