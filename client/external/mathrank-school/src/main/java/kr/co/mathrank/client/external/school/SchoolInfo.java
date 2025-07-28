package kr.co.mathrank.client.external.school;

public record SchoolInfo(
	// 행정표준코드
	String SD_SCHUL_CODE,
	// 학교명
	String SCHUL_NM,
	// 학교종류명
	String SCHUL_KND_SC_NM,
	// 시도명
	String LCTN_SC_NM,
	// 도로명 주소
	String ORG_RDNMA
) {
	public static SchoolInfo none() {
		return new SchoolInfo(null, null, null, null, null);
	}
}
