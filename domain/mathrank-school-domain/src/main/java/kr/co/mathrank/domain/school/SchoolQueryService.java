package kr.co.mathrank.domain.school;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.client.external.school.RequestType;
import kr.co.mathrank.client.external.school.SchoolClient;
import kr.co.mathrank.domain.school.dto.SchoolResponse;
import kr.co.mathrank.domain.school.dto.SchoolResponses;
import lombok.RequiredArgsConstructor;

@Service
@Validated
@RequiredArgsConstructor
@CacheConfig(cacheNames = SchoolQueryCacheConfiguration.SCHOOL_QUERY_BY_SCHOOL_NAME_AND_PAGE_CACHE)
public class SchoolQueryService {
	private final SchoolCityQueryManager schoolCityQueryManager;
	private final SchoolClient schoolClient;

	// 단일 "구"로 필터링 하기 위한 포맷
	// " 서구 "
	private static final String DISTRICT_FORMAT = " %s ";

	// 학교 정보는 정적이며 클라이언트에서 고정된 pageIndex, pageSize로 호출, 따라서 해당 결과 캐싱
	@Cacheable(key = "#schoolName + '::' + #pageIndex + '::' + #pageSize")
	public SchoolResponses searchSchools(
		String schoolName,
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
		final String formattedDistrict = String.format(DISTRICT_FORMAT, district);

		return new SchoolResponses(schoolCityQueryManager.getSchoolsIn(cityName).getSchoolInfo()
			.stream()
			.filter(schoolInfo -> schoolInfo.ORG_RDNMA() != null)
			.filter(schoolInfo -> schoolInfo.ORG_RDNMA().contains(formattedDistrict))
			.map(SchoolResponse::from)
			.toList());
	}
}
