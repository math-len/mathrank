package kr.co.mathrank.domain.rank.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TierTest {
	@Test
	void 다이아야일껄() {
		// 상위 0,1 퍼 이면 다이아 1
		Assertions.assertEquals(Tier.DIAMOND_STAR_1, Tier.getMatchTier(1, 1000));
		// 상위 1 퍼 이면 다이아 1
		Assertions.assertEquals(Tier.DIAMOND_STAR_1, Tier.getMatchTier(1, 100));
		// 상위 1.5 퍼 이면 다이아 2
		Assertions.assertEquals(Tier.DIAMOND_STAR_2, Tier.getMatchTier(2, 100));
		// 상위 2 퍼 이면 다이아 2
		Assertions.assertEquals(Tier.DIAMOND_STAR_2, Tier.getMatchTier(2, 100));
	}

	@Test
	void 뉴비일껄() {
		// 상위 70퍼 이면 뉴비일껄
		Assertions.assertEquals(Tier.NOOBIE_1, Tier.getMatchTier(70, 100));
	}

	@Test
	void 백분위에_안들면_널일껄() {
		// 백분위에 안들면 NONE으로 출력할껄
		// 로그도 출력할껄
		Assertions.assertEquals(Tier.NONE, Tier.getMatchTier(100, 1));
	}
}