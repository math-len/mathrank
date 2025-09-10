package kr.co.mathrank.domain.school;

import org.springframework.stereotype.Service;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.client.external.school.RequestType;
import kr.co.mathrank.client.external.school.SchoolClient;
import kr.co.mathrank.domain.school.dto.SchoolResponse;
import kr.co.mathrank.domain.school.dto.SchoolResponses;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SchoolQueryService {
	private final SchoolCityQueryManager schoolCityQueryManager;
	private final SchoolClient schoolClient;

	// 단일 "구"로 필터링 하기 위한 포맷
	// " 서구 "
	private static final String DISTRICT_FORMAT = " %s ";

	public SchoolResponses searchSchools(
		@NotNull String schoolName,
		@NotNull Integer pageIndex,
		@NotNull Integer pageSize
		) {
		return new SchoolResponses(schoolClient.getSchools(RequestType.JSON.getType(), pageIndex, pageSize, schoolName)
			.getSchoolInfo().stream()
			.map(SchoolResponse::from)
			.toList());
	}

	public SchoolResponses searchSchoolsBySchoolByAddress(
		@NotNull final String cityName,
		@NotNull final String district
	) {
		final String formattedDistrict = district.replaceAll(DISTRICT_FORMAT, "");

		return new SchoolResponses(schoolCityQueryManager.getSchoolsIn(cityName).getSchoolInfo()
			.stream()
			.filter(schoolInfo -> schoolInfo.ORG_RDNMA() != null)
			.filter(schoolInfo -> schoolInfo.ORG_RDNMA().contains(formattedDistrict))
			.map(SchoolResponse::from)
			.toList());
	}
}
