package kr.co.mathrank.domain.rank.dto;

import kr.co.mathrank.domain.rank.entity.Tier;

public record RankQueryResult(
	Long rank,
	Tier tier,
	Long score,
	Long totalUserCount
) {
	public static RankQueryResult of(final Long rank, final Tier tier, final Long score, final long totalUserCount) {
		return new RankQueryResult(rank, tier, score, totalUserCount);
	}
}
