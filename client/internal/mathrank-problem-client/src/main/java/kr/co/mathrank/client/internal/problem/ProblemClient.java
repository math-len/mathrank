package kr.co.mathrank.client.internal.problem;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestClient;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Validated
public class ProblemClient {
	private static final String URL_FORMAT = "%s:%s";
	private final RestClient problemClient;

	ProblemClient(final ProblemClientProperties properties) {
		this.problemClient = RestClient.builder()
			.baseUrl(getUrlFormat(properties.host, properties.port))
			.build();
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

	public boolean isExist(final Long problemId) {
		final HttpStatusCode statusCode = problemClient.head()
			.uri(uriBuilder -> uriBuilder
				.path("/api/inner/v1/problem")
				.queryParam("problemId", problemId)
				.build())
			.retrieve()
			.toBodilessEntity()
			.getStatusCode();

		return statusCode.isSameCodeAs(HttpStatus.OK);
	}

	private String getUrlFormat(final String host, final Integer port) {
		return URL_FORMAT.formatted(host, port);
	}

	@Getter
	@Configuration
	@ConfigurationProperties("client.problem")
	@NoArgsConstructor
	@Setter
	static class ProblemClientProperties {
		private String host = "http://localhost";
		private Integer port = 8080;
	}
}
