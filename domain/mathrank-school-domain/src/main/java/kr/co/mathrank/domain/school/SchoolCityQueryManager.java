package kr.co.mathrank.domain.school;

import org.springframework.stereotype.Component;

import kr.co.mathrank.client.external.school.RequestType;
import kr.co.mathrank.client.external.school.SchoolClient;
import kr.co.mathrank.client.external.school.SchoolResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class SchoolCityQueryManager {
	private final SchoolClient schoolClient;

	public SchoolResponse getSchoolsIn(final String cityName) {
		return schoolClient.getSchoolsByCityName(RequestType.JSON.getType(), cityName);
	}
}
