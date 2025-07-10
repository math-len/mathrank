package kr.co.mathrank.client.external.school;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SchoolClient {
	@Value("${neice.school.key:}")
	private String key;
	private final RestClient restClient;

	public SchoolClient() {
		restClient = RestClient.builder()
			.baseUrl("https://open.neis.go.kr")
			.build();
	}

	public SchoolResponse getSchools(String type, Integer pageIndex, Integer pageSize, String schoolName) {
		return restClient.get()
			.uri(uriBuilder -> uriBuilder.path("/hub/schoolInfo")
				.queryParam("Type", type)
				.queryParam("pIndex", pageIndex)
				.queryParam("pSize", pageSize)
				.queryParam("KEY", key)
				.queryParam("SCHUL_NM", schoolName).build())
			.retrieve()
			.body(SchoolResponse.class);
	}
}
