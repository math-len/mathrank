package kr.co.mathrank.client.external.school;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import kr.co.mathrank.client.exception.aspect.Client;
import lombok.RequiredArgsConstructor;

@Component
@Client
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
		if (schoolCode == null || schoolCode.isBlank()) {
			return Optional.empty();
		}

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
	 * 도시 이름 기반으로 학교 목록을 조회합니다.
	 * NEIS API는 도시 이름으로 조회 시 최대 1000개의 결과만 반환하므로, 모든 결과를 가져오기 위해 페이지 크기를 1000으로 고정합니다.
	 *
	 * @param type 요청 타입 (ex: "json")
	 * @param cityName 도시 전체 이름 (ex: "부산광역시", "서울특별시")
	 * @return 해당 도시의 학교 정보가 담긴 응답 객체
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
