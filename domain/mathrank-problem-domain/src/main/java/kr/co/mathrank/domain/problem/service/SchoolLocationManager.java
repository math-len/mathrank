package kr.co.mathrank.domain.problem.service;

import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.client.external.school.RequestType;
import kr.co.mathrank.client.external.school.SchoolClient;
import kr.co.mathrank.client.external.school.SchoolInfo;
import kr.co.mathrank.domain.problem.exception.CannotFoundSchoolException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Validated
@RequiredArgsConstructor
class SchoolLocationManager {
	private final SchoolClient schoolClient;

	public String getSchoolLocation(@NotNull final String schoolCode) {
		return schoolClient.getSchool(RequestType.JSON.getType(), schoolCode)
			.map(SchoolInfo::ORG_RDNMA)
			.orElseGet(() -> {
				log.warn("[SchoolLocationManager.getSchoolLocation] cannot found school - code: {}", schoolCode);
				return null;
			});
	}
}
