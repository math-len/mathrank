package kr.co.mathrank.domain.rank.dto;

public record RankQueryResult(
	Integer rank,
	Long totalUserCount
) {
	public static RankQueryResult of(final int rank, final long totalUserCount) {
		return new RankQueryResult(rank, totalUserCount);
	}
}
