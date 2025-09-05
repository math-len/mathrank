package kr.co.mathrank.domain.rank.dto;

public record RankQueryResult(
	Integer rank,
	Long score,
	Long totalUserCount
) {
	public static RankQueryResult of(final int rank, final Long score, final long totalUserCount) {
		return new RankQueryResult(rank, score, totalUserCount);
	}
}
