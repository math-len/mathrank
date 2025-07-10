package kr.co.mathrank.app.api.school;

import kr.co.mathrank.client.external.school.SchoolInfo;

public record SchoolResponse(
	String schoolName,
	String schoolCode,
	String schoolKind,
	String city
) {
	public static SchoolResponse from(final SchoolInfo schoolInfo) {
		return new SchoolResponse(
			schoolInfo.SCHUL_NM(),
			schoolInfo.SD_SCHUL_CODE(),
			schoolInfo.SCHUL_KND_SC_NM(),
			schoolInfo.LCTN_SC_NM()
		);
	}
}
