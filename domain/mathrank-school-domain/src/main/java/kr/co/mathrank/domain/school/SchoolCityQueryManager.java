package kr.co.mathrank.domain.school;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import kr.co.mathrank.client.external.school.RequestType;
import kr.co.mathrank.client.external.school.SchoolClient;
import kr.co.mathrank.client.external.school.SchoolResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@CacheConfig(cacheNames = SchoolQueryCacheConfiguration.SCHOOL_QUERY_BY_CITY_CACHE)
class SchoolCityQueryManager {
	private final SchoolClient schoolClient;

	@Cacheable(key = "#cityName")
	public SchoolResponse getSchoolsIn(final String cityName) {
		return schoolClient.getSchoolsByCityName(RequestType.JSON.getType(), cityName);
	}
}
