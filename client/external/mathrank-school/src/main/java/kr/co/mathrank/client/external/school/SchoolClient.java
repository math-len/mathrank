package kr.co.mathrank.client.external.school;

import java.util.Optional;

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

	public Optional<SchoolInfo> getSchool(final String type, final String schoolCode) {
		// SD_SCHUL_CODE
		final SchoolResponse response = restClient.get()
			.uri(uriBuilder -> uriBuilder.path("/hub/schoolInfo")
				.queryParam("Type", type)
				.queryParam("pIndex", 1)
				.queryParam("pSize", 1)
				.queryParam("KEY", key)
				.queryParam("SD_SCHUL_CODE", schoolCode)
				.build())
			.retrieve()
			.body(SchoolResponse.class);
		return response.getSchoolInfo().isEmpty() ? Optional.empty() : Optional.of(response.getSchoolInfo().getFirst());
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

	/**
	 *
	 * @param type
	 * @param cityName ex) 부산광역시, 서울특별시, 인천광역시
	 * @return
	 */
	public SchoolResponse getSchoolsByCityName(String type, String cityName) {
		return restClient.get()
			.uri(uriBuilder -> uriBuilder.path("/hub/schoolInfo")
				.queryParam("Type", type)
				.queryParam("pIndex", 1)
				.queryParam("pSize", 1000)
				.queryParam("KEY", key)
				.queryParam("LCTN_SC_NM", cityName)
				.build())
			.retrieve()
			.body(SchoolResponse.class);
	}
}
