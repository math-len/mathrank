package kr.co.mathrank.client.internal.course;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestClient;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.client.config.RestClientResponseDecorator;
import kr.co.mathrank.client.config.TimeoutConfiguredClient;
import kr.co.mathrank.client.response.ClientResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CourseClient extends TimeoutConfiguredClient {
	private static final String URL_FORMAT = "%s:%s";

	private final RestClient restClient;
	private final RestClientResponseDecorator responseDecorator;

	CourseClient(final CourseClientProperties properties, final RestClientResponseDecorator restClientResponseDecorator) {
		final String baseURL = URL_FORMAT.formatted(properties.getHost(), properties.getPort());
		log.info("[CourseClient.new] init : {}", baseURL);
		this.restClient = RestClient.builder()
			.requestFactory(configureTimeoutConfiguration(
				Duration.ofSeconds(properties.getConnectionTimeoutSeconds()),
				Duration.ofSeconds(properties.getReadTimeoutSeconds()))
			)
			.baseUrl(baseURL)
			.build();
		this.responseDecorator = restClientResponseDecorator;
	}

	@Deprecated
	public CourseQueryContainsParentsResult getParentCourses(final String coursePath) {
		log.info("[CourseClient.getParentCourses]: called - coursePath: {}", coursePath);
		return restClient.get()
			.uri(uriBuilder -> uriBuilder
				.path("/api/inner/v1/course/parents")
				.queryParam("coursePath", coursePath)
				.build())
			.retrieve()
			.body(CourseQueryContainsParentsResult.class);
	}

	public ClientResponse<CourseQueryContainsParentsResult> getParentCoursesResponse(final String coursePath) {
		return responseDecorator.wrap(() -> getParentCourses(coursePath));
	}

	@Getter
	@Configuration
	@ConfigurationProperties("client.course")
	@NoArgsConstructor
	@Setter
	@Validated
	@ToString
	static class CourseClientProperties {
		@NotNull
		private String host;
		@NotNull
		private Integer port;
		@NotNull
		private Integer connectionTimeoutSeconds;
		@NotNull
		private Integer readTimeoutSeconds;
	}
}
