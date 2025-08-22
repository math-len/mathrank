package kr.co.mathrank.client.internal.course;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestClient;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
public class CourseClient {
	private static final String URL_FORMAT = "%s:%s";

	private final RestClient restClient;

	CourseClient(final CourseClientProperties properties) {
		this.restClient = RestClient.builder()
			.baseUrl(getUrlFormat(properties.host, properties.port))
			.build();
	}

	public CourseQueryContainsParentsResult getParentCourses(final String coursePath) {
		return restClient.get()
			.uri(uriBuilder -> uriBuilder
				.path("/api/inner/v1/course/parents")
				.queryParam("coursePath", coursePath)
				.build())
			.retrieve()
			.body(CourseQueryContainsParentsResult.class);
	}

	private String getUrlFormat(final String host, final Integer port) {
		return URL_FORMAT.formatted(host, port);
	};

	@Getter
	@Configuration
	@ConfigurationProperties("client.course")
	@NoArgsConstructor
	@Setter
	@Validated
	static class CourseClientProperties {
		@NotNull
		private String host;
		@NotNull
		private Integer port;
	}
}
