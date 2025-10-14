package kr.co.mathrank.domain.rank.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.co.mathrank.domain.rank.dto.RankQueryResult;
import kr.co.mathrank.domain.rank.entity.Solver;
import kr.co.mathrank.domain.rank.repository.SolveLogRepository;
import kr.co.mathrank.domain.rank.repository.SolverRepository;

@SpringBootTest
class RankQueryServiceTest {
	@Autowired
	private RankQueryService rankQueryService;

	@Test
	void 없는_사용자_조회시_총_사용자_수_말곤_전부_null() {
		final RankQueryResult result = rankQueryService.getRank(2L);

		Assertions.assertAll(
			() -> Assertions.assertNull(result.rank()),
			() -> Assertions.assertNull(result.score()),
			() -> Assertions.assertNull(result.tier()),
			() -> Assertions.assertNotNull(result.totalUserCount())
		);
	}
}
