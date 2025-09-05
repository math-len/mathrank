package kr.co.mathrank.domain.rank.dto;

import kr.co.mathrank.domain.rank.entity.Tier;

public record RankQueryResult(
	Integer rank,
	Tier tier,
	Long score,
	Long totalUserCount
) {
	public static RankQueryResult of(final int rank, final Tier tier, final Long score, final long totalUserCount) {
		return new RankQueryResult(rank, tier, score, totalUserCount);
	}
}
