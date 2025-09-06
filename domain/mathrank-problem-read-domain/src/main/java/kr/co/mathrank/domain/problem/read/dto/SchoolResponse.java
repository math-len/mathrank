package kr.co.mathrank.domain.problem.read.dto;

import kr.co.mathrank.client.external.school.SchoolInfo;

public record SchoolResponse(
	String schoolCode,
	String schoolName,
	String schoolKind,
	String schoolLocation
) {
	public static SchoolResponse from(final SchoolInfo schoolInfo) {
		return new SchoolResponse(
			schoolInfo.SD_SCHUL_CODE(),
			schoolInfo.SCHUL_NM(),
			schoolInfo.SCHUL_KND_SC_NM(),
			schoolInfo.ORG_RDNMA());
	}
}
