package kr.co.mathrank.domain.rank.dto;

public record SchoolRankQueryResult(
	String schoolCode,
	Long score,
	Long rank,
	Long memberCount
) {
}
