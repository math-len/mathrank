package kr.co.mathrank.domain.rank.entity;

import java.util.Arrays;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public enum Tier {
	// stars
	DIAMOND_STAR_1(1F),
	DIAMOND_STAR_2(2F),
	DIAMOND_STAR_3(4F),
	DIAMOND_STAR_4(6F),
	DIAMOND_STAR_5(8F),

	GOLD_STAR_1(10F),
	GOLD_STAR_2(12F),
	GOLD_STAR_3(14F),
	GOLD_STAR_4(16F),
	GOLD_STAR_5(18F),

	SILVER_STAR_1(20F),
	SILVER_STAR_2(22F),
	SILVER_STAR_3(24F),
	SILVER_STAR_4(26F),
	SILVER_STAR_5(28F),

	BRONZE_STAR_1(30F),
	BRONZE_STAR_2(32F),
	BRONZE_STAR_3(34F),
	BRONZE_STAR_4(36F),
	BRONZE_STAR_5(38F),

	// medals
	GOLD_MEDAL_1(40F),
	GOLD_MEDAL_2(42F),
	GOLD_MEDAL_3(44F),
	GOLD_MEDAL_4(46F),
	GOLD_MEDAL_5(48F),

	SILVER_MEDAL_1(50F),
	SILVER_MEDAL_2(52F),
	SILVER_MEDAL_3(54F),
	SILVER_MEDAL_4(56F),
	SILVER_MEDAL_5(58F),

	COPPER_MEDAL_1(60F),
	COPPER_MEDAL_2(62F),
	COPPER_MEDAL_3(64F),
	COPPER_MEDAL_4(66F),
	COPPER_MEDAL_5(68F),

	// noobies
	NOOBIE_1(70F),
	NOOBIE_2(75F),
	NOOBIE_3(80F),
	NOOBIE_4(85F),
	NOOBIE_5(90F),

	NONE(100);

	private final float percentage;

	public static Tier getMatchTier(final long rank, final long totalUserCount) {
		final float top = (float) rank / totalUserCount * 100;

		// 첫번째로 만족하는 놈으로 결정
		return Arrays.stream(Tier.values())
			.filter(tier -> tier.percentage >= top)
			.findFirst()
			.orElseGet(() -> {
				log.warn("[Tier.getMatchTier] cannot find match tier - rank: {}, totalUserCount: {}", rank, totalUserCount);
				return NONE;
			});
	}
}
