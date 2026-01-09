package kr.co.mathrank.rank.read;

import kr.co.mathrank.domain.rank.dto.SchoolRankPageResult;

public record SchoolRankPageResponse(
	String schoolCode,
	String schoolName,
	Long score,
	Long rank,
	Long memberCount
) {
	public static SchoolRankPageResponse from(final SchoolRankPageResult result) {
		return new SchoolRankPageResponse(
			result.schoolCode(),
			result.schoolName(),
			result.score(),
			result.rank(),
			result.memberCount()
		);
	}
}
