package kr.co.mathrank.domain.problem.single.service;

import java.time.Duration;
import java.util.Collections;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import kr.co.mathrank.client.internal.problem.ProblemClient;
import kr.co.mathrank.domain.problem.single.dto.SingleProblemSolveResult;
import kr.co.mathrank.domain.problem.single.dto.SingleProblemStatisticsResult;
import kr.co.mathrank.domain.problem.single.entity.SingleProblem;
import kr.co.mathrank.domain.problem.single.repository.ChallengerRepository;
import kr.co.mathrank.domain.problem.single.repository.SingleProblemRepository;

@SpringBootTest
@Testcontainers
class SingleProblemStatisticsServiceTest {
	@Autowired
	private SingleProblemService singleProblemService;
	@Autowired
	private SingleProblemRepository singleProblemRepository;
	@MockitoBean
	private ProblemClient problemClient;
	@Autowired
	private ChallengerRepository challengerRepository;

	@Container
	static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0.42")
		.withDatabaseName("testdb")
		.withUsername("user")
		.withPassword("password");
	@Autowired
	private ChallengeLogSaveManager challengeLogSaveManager;
	@Autowired
	private SingleProblemStatisticsService singleProblemStatisticsService;

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry registry) {
		mysql.start();
		registry.add("spring.datasource.url", mysql::getJdbcUrl);
		registry.add("spring.datasource.username", mysql::getUsername);
		registry.add("spring.datasource.password", mysql::getPassword);
	}

	@Test
	void 통계_조회_시_첫번쨰_기록만_출력된다() {
		final Long singleProblemId = singleProblemRepository.save(SingleProblem.of(1L, "test", 2L)).getId();

		// 사용자 1번이 세번 품
		challengeLogSaveManager.saveLog(singleProblemId, 1L,
			new SingleProblemSolveResult(true, Collections.emptySet(), Collections.emptyList()),
			Duration.ofMinutes(10L));
		challengeLogSaveManager.saveLog(singleProblemId, 1L,
			new SingleProblemSolveResult(false, Collections.emptySet(), Collections.emptyList()),
			Duration.ofMinutes(20L));
		challengeLogSaveManager.saveLog(singleProblemId, 1L,
			new SingleProblemSolveResult(true, Collections.emptySet(), Collections.emptyList()),
			Duration.ofMinutes(30L));

		final SingleProblemStatisticsResult result = singleProblemStatisticsService.loadFirstTrySucceedStatistics(singleProblemId);
		Assertions.assertAll(
			() -> Assertions.assertEquals(Duration.ofMinutes(10L), result.elapsedTimes().getFirst()),
			() -> Assertions.assertEquals(1, result.elapsedTimes().size()),
			() -> Assertions.assertEquals(Duration.ofMinutes(10L), result.averageElapsedTime())
		);
	}

	@Test
	void 통계_조회시_실패한_풀이는_포함하지_않는다() {
		final Long singleProblemId = singleProblemRepository.save(SingleProblem.of(1L, "test", 2L)).getId();

		// 실패한 사용자
		challengeLogSaveManager.saveLog(singleProblemId, 1L,
			new SingleProblemSolveResult(false, Collections.emptySet(), Collections.emptyList()),
			Duration.ofMinutes(10L));

		final SingleProblemStatisticsResult result = singleProblemStatisticsService.loadFirstTrySucceedStatistics(singleProblemId);
		Assertions.assertAll(
			() -> Assertions.assertEquals(0, result.elapsedTimes().size()),
			() -> Assertions.assertEquals(Duration.ZERO, result.averageElapsedTime()),
			() -> Assertions.assertEquals(1, result.distinctUserCount())
		);
	}

	@Test
	void 통계_조회_시_평균이_정상_출력된다() {
		final Long singleProblemId = singleProblemRepository.save(SingleProblem.of(1L, "test", 2L)).getId();

		challengeLogSaveManager.saveLog(singleProblemId, 1L,
			new SingleProblemSolveResult(true, Collections.emptySet(), Collections.emptyList()),
			Duration.ofMinutes(10L));
		challengeLogSaveManager.saveLog(singleProblemId, 2L,
			new SingleProblemSolveResult(true, Collections.emptySet(), Collections.emptyList()),
			Duration.ofMinutes(20L));
		challengeLogSaveManager.saveLog(singleProblemId, 3L,
			new SingleProblemSolveResult(true, Collections.emptySet(), Collections.emptyList()),
			Duration.ofMinutes(30L));
		// 4번 사용자가 풀이 실패!
		challengeLogSaveManager.saveLog(singleProblemId, 4L,
			new SingleProblemSolveResult(false, Collections.emptySet(), Collections.emptyList()),
			Duration.ofMinutes(40L));

		final SingleProblemStatisticsResult result = singleProblemStatisticsService.loadFirstTrySucceedStatistics(singleProblemId);
		Assertions.assertAll(
			() -> Assertions.assertEquals(3, result.elapsedTimes().size()), // 성공한 사용자의 풀이시간만 출력
			() -> Assertions.assertEquals(4, result.distinctUserCount()), // 총 풀이 사용자 수는 4명
			() -> Assertions.assertEquals(Duration.ofMinutes(20L), result.averageElapsedTime()) // 10, 20, 30 의 평균 20
		);
	}

	@AfterEach
	void clear() {
		singleProblemRepository.deleteAll();
	}
}
