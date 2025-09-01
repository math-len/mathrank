package kr.co.mathrank.domain.problem.single.service;

import java.time.Duration;
import java.util.Collections;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.co.mathrank.domain.problem.single.dto.SingleProblemRankQuery;
import kr.co.mathrank.domain.problem.single.dto.SingleProblemRankResult;
import kr.co.mathrank.domain.problem.single.dto.SingleProblemSolveResult;
import kr.co.mathrank.domain.problem.single.entity.SingleProblem;
import kr.co.mathrank.domain.problem.single.repository.SingleProblemRepository;

@SpringBootTest
class SingleProblemRankQueryServiceTest {
	@Autowired
	private SingleProblemRankQueryService singleProblemRankQueryService;
	@Autowired
	private SingleProblemRepository singleProblemRepository;
	@Autowired
	private ChallengeLogSaveManager challengeLogSaveManager;

	@Test
	void 등수_처리할때_실패한_사용자도_총_시도_수에_포함() {
		final Long singleProblemId = singleProblemRepository.save(SingleProblem.of(1L, "test", 2L)).getId();

		challengeLogSaveManager.saveLog(singleProblemId, 1L,
			new SingleProblemSolveResult(1L, true, Collections.emptySet(), Collections.emptyList()),
			Duration.ofMinutes(10L));
		challengeLogSaveManager.saveLog(singleProblemId, 2L,
			new SingleProblemSolveResult(1L, true, Collections.emptySet(), Collections.emptyList()),
			Duration.ofMinutes(20L));
		challengeLogSaveManager.saveLog(singleProblemId, 3L,
			new SingleProblemSolveResult(1L, true, Collections.emptySet(), Collections.emptyList()),
			Duration.ofMinutes(30L));
		// 4번 사용자만 실패함
		challengeLogSaveManager.saveLog(singleProblemId, 4L,
			new SingleProblemSolveResult(1L, false, Collections.emptySet(), Collections.emptyList()),
			Duration.ofMinutes(30L));

		final SingleProblemRankResult result = singleProblemRankQueryService.queryRank(
			new SingleProblemRankQuery(1L, singleProblemId));

		// 총 시도한 사용자 수는 4명이여야 한다.
		Assertions.assertEquals(4, result.distinctUserCount());
	}

	@Test
	void 실패한_사용자는_응답에_등수를_포함하지_않는다() {
		final Long singleProblemId = singleProblemRepository.save(SingleProblem.of(1L, "test", 2L)).getId();

		challengeLogSaveManager.saveLog(singleProblemId, 1L,
			new SingleProblemSolveResult(1L, true, Collections.emptySet(), Collections.emptyList()),
			Duration.ofMinutes(10L));
		challengeLogSaveManager.saveLog(singleProblemId, 2L,
			new SingleProblemSolveResult(1L, true, Collections.emptySet(), Collections.emptyList()),
			Duration.ofMinutes(20L));
		challengeLogSaveManager.saveLog(singleProblemId, 3L,
			new SingleProblemSolveResult(1L, true, Collections.emptySet(), Collections.emptyList()),
			Duration.ofMinutes(30L));
		// 4번 사용자만 실패함
		challengeLogSaveManager.saveLog(singleProblemId, 4L,
			new SingleProblemSolveResult(1L, false, Collections.emptySet(), Collections.emptyList()),
			Duration.ofMinutes(30L));

		// 4번 사용자 조회
		final SingleProblemRankResult result = singleProblemRankQueryService.queryRank(
			new SingleProblemRankQuery(4L, singleProblemId));

		Assertions.assertNull(result.myRank());
	}

	@Test
	void 등수처리_확인() {
		final Long singleProblemId = singleProblemRepository.save(SingleProblem.of(1L, "test", 2L)).getId();

		challengeLogSaveManager.saveLog(singleProblemId, 1L,
			new SingleProblemSolveResult(1L,true, Collections.emptySet(), Collections.emptyList()),
			Duration.ofMinutes(10L));
		challengeLogSaveManager.saveLog(singleProblemId, 2L,
			new SingleProblemSolveResult(1L, true, Collections.emptySet(), Collections.emptyList()),
			Duration.ofMinutes(20L));
		challengeLogSaveManager.saveLog(singleProblemId, 3L,
			new SingleProblemSolveResult(1L, true, Collections.emptySet(), Collections.emptyList()),
			Duration.ofMinutes(30L));
		// 4번 사용자만 실패함
		challengeLogSaveManager.saveLog(singleProblemId, 4L,
			new SingleProblemSolveResult(1L, false, Collections.emptySet(), Collections.emptyList()),
			Duration.ofMinutes(30L));

		// 1번 사용자 등수 조회
		final SingleProblemRankResult result1 = singleProblemRankQueryService.queryRank(
			new SingleProblemRankQuery(1L, singleProblemId));
		// 2번 사용자 등수 조회
		final SingleProblemRankResult result2 = singleProblemRankQueryService.queryRank(
			new SingleProblemRankQuery(2L, singleProblemId));
		// 3번 사용자 등수 조회
		final SingleProblemRankResult result3 = singleProblemRankQueryService.queryRank(
			new SingleProblemRankQuery(3L, singleProblemId));
		final SingleProblemRankResult result4 = singleProblemRankQueryService.queryRank(
			new SingleProblemRankQuery(4L, singleProblemId));

		// 총 시도한 사용자 수는 4명이여야 한다.
		Assertions.assertAll(
			() -> Assertions.assertEquals(1, result1.myRank()),  // 1등
			() -> Assertions.assertEquals(2, result2.myRank()),  // 2등
			() -> Assertions.assertEquals(3, result3.myRank()),  // 3등
			() -> Assertions.assertEquals(null, result4.myRank())// 풀이에 실패하면 출력하지 않음
		);
	}

	@Test
	void 동점자가_있을_경우_같은_등수() {
		final Long singleProblemId = singleProblemRepository.save(SingleProblem.of(1L, "test", 2L)).getId();
		challengeLogSaveManager.saveLog(singleProblemId, 1L,
			new SingleProblemSolveResult(1L, true, Collections.emptySet(), Collections.emptyList()),
			Duration.ofMinutes(10L));
		challengeLogSaveManager.saveLog(singleProblemId, 2L,
			new SingleProblemSolveResult(1L, true, Collections.emptySet(), Collections.emptyList()),
			Duration.ofMinutes(10L));
		challengeLogSaveManager.saveLog(singleProblemId, 3L,
			new SingleProblemSolveResult(1L, true, Collections.emptySet(), Collections.emptyList()),
			Duration.ofMinutes(10L));

		// 1번 사용자 등수 조회
		final SingleProblemRankResult result1 = singleProblemRankQueryService.queryRank(
			new SingleProblemRankQuery(1L, singleProblemId));
		// 2번 사용자 등수 조회
		final SingleProblemRankResult result2 = singleProblemRankQueryService.queryRank(
			new SingleProblemRankQuery(2L, singleProblemId));
		// 3번 사용자 등수 조회
		final SingleProblemRankResult result3 = singleProblemRankQueryService.queryRank(
			new SingleProblemRankQuery(3L, singleProblemId));

		Assertions.assertAll(
			() -> Assertions.assertEquals(1, result1.myRank()),
			() -> Assertions.assertEquals(1, result2.myRank()),
			() -> Assertions.assertEquals(1, result3.myRank())
		);
	}

	@Test
	void 동점자가_있을_경우_동점자_다음_등수는_동점자수를_합친것으로_결정된다() {
		final Long singleProblemId = singleProblemRepository.save(SingleProblem.of(1L, "test", 2L)).getId();
		challengeLogSaveManager.saveLog(singleProblemId, 1L,
			new SingleProblemSolveResult(1L, true, Collections.emptySet(), Collections.emptyList()),
			Duration.ofMinutes(10L));
		challengeLogSaveManager.saveLog(singleProblemId, 2L,
			new SingleProblemSolveResult(1L, true, Collections.emptySet(), Collections.emptyList()),
			Duration.ofMinutes(10L));
		challengeLogSaveManager.saveLog(singleProblemId, 3L,
			new SingleProblemSolveResult(1L, true, Collections.emptySet(), Collections.emptyList()),
			Duration.ofMinutes(10L));

		// 동점자들보다 점수가 낮음
		challengeLogSaveManager.saveLog(singleProblemId, 4L,
			new SingleProblemSolveResult(1L, true, Collections.emptySet(), Collections.emptyList()),
			Duration.ofMinutes(30L));

		final SingleProblemRankResult result1 = singleProblemRankQueryService.queryRank(
			new SingleProblemRankQuery(4L, singleProblemId));

		// 동점자 1, 2, 3 다음의 등수인 4등
		Assertions.assertEquals(4, result1.myRank());
	}

	@Test
	void 첫_시도만_등수에_결정된다() {
		final Long singleProblemId = singleProblemRepository.save(SingleProblem.of(1L, "test", 2L)).getId();

		challengeLogSaveManager.saveLog(singleProblemId, 1L,
			new SingleProblemSolveResult(1L, true, Collections.emptySet(), Collections.emptyList()),
			Duration.ofMinutes(20L));
		challengeLogSaveManager.saveLog(singleProblemId, 2L,
			new SingleProblemSolveResult(1L, true, Collections.emptySet(), Collections.emptyList()),
			Duration.ofMinutes(10L));
		challengeLogSaveManager.saveLog(singleProblemId, 3L,
			new SingleProblemSolveResult(1L, true, Collections.emptySet(), Collections.emptyList()),
			Duration.ofMinutes(10L));

		// 1번 사용자가 한번 더 시도함 ( 제일 빨리 성공! )
		challengeLogSaveManager.saveLog(singleProblemId, 1L,
			new SingleProblemSolveResult(1L, true, Collections.emptySet(), Collections.emptyList()),
			Duration.ofMinutes(0L));

		final SingleProblemRankResult result1 = singleProblemRankQueryService.queryRank(
			new SingleProblemRankQuery(1L, singleProblemId));

		// 첫번째 시도만 랭크에 적용된다
		Assertions.assertEquals(3, result1.myRank());
	}

	@AfterEach
	void clear() {
		singleProblemRepository.deleteAll();
	}
}
