package kr.co.mathrank.rank.read;

import kr.co.mathrank.domain.rank.dto.SchoolRankQueryResult;

public record SchoolRankPageResponse(
	String schoolCode,
	String schoolName,
	Long score,
	Long rank,
	Long memberCount
) {
	public static SchoolRankPageResponse from(final SchoolRankQueryResult result, final String schoolName) {
		return new SchoolRankPageResponse(
			result.schoolCode(),
			schoolName,
			result.score(),
			result.rank(),
			result.memberCount()
		);
	}
}
