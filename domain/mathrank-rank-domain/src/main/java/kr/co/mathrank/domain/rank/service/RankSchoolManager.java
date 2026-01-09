package kr.co.mathrank.domain.rank.service;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.client.external.school.RequestType;
import kr.co.mathrank.client.external.school.SchoolClient;
import kr.co.mathrank.client.external.school.SchoolInfo;
import kr.co.mathrank.domain.rank.RankDomainConfiguration;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RankSchoolManager {
	private final SchoolClient schoolClient;

	@Cacheable(
		cacheNames = RankDomainConfiguration.SCHOOL_NAME_CACHE,
		key = "'schoolCode::' + #schoolCode",
		unless = "#result == null" // 결과가 null이면 캐싱하지 않음 ( 다음에 다시 호출 )
	)
	public String getSchoolNameByCode(@NotNull @Valid final String schoolCode) {
		return schoolClient.getSchool(RequestType.JSON.getType(), schoolCode)
			.orElseGet(() -> SchoolInfo.none()).SCHUL_NM();
	}
}
