package kr.co.mathrank.rank.read;

import kr.co.mathrank.domain.rank.dto.RankQueryResult;
import kr.co.mathrank.domain.rank.entity.Tier;

public record RankResponse(
	Integer rank,
	Tier tier,
	Long score,
	Long totalUserCount
) {
	static RankResponse from(RankQueryResult result) {
		return new RankResponse(
			result.rank(),
			result.tier(),
			result.score(),
			result.totalUserCount()
		);
	}
}
