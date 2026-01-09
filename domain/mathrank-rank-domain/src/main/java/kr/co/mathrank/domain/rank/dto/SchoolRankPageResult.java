package kr.co.mathrank.domain.rank.dto;

public record SchoolRankPageResult(
	String schoolCode,
	String schoolName,
	Long score,
	Long rank,
	Long memberCount
) {
	public static SchoolRankPageResult from(final SchoolRankQueryResult result, final String schoolName) {
		return new SchoolRankPageResult(
			result.schoolCode(),
			schoolName,
			result.score(),
			result.rank(),
			result.memberCount()
		);
	}
}