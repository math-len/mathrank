package kr.co.mathrank.domain.problem.single.service;

import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

import kr.co.mathrank.client.internal.problem.SolveResult;
import kr.co.mathrank.domain.problem.single.entity.SingleProblem;
import kr.co.mathrank.domain.problem.single.repository.ChallengeLogRepository;
import kr.co.mathrank.domain.problem.single.repository.SingleProblemRepository;

@SpringBootTest(
	properties = """
		snowflake.node.id=111
		spring.jpa.show-sql=true
		spring.jpa.properties.hibernate.format_sql=true
		spring.jpa.hibernate.ddl-auto=create
		"""
)

class ChallengeLogSaveManagerTest {
	@Autowired
	private ChallengeLogSaveManager challengeLogSaveManager;
	@Autowired
	private SingleProblemRepository singleProblemRepository;
	@Autowired
	private ChallengeLogRepository challengeLogRepository;

	@Container
	static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0.42")
		.withDatabaseName("testdb")
		.withUsername("user")
		.withPassword("password");

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry registry) {
		mysql.start();
		registry.add("spring.datasource.url", mysql::getJdbcUrl);
		registry.add("spring.datasource.username", mysql::getUsername);
		registry.add("spring.datasource.password", mysql::getPassword);
	}

	@Test
	void 여러_사용자들이_문제풀이를_동시에_성공할때_동시성문제_없도록_한다() throws InterruptedException {
		final Long singleProblemId = singleProblemRepository.save(SingleProblem.of(1L, 2L)).getId();

		// 로그는 총 1000개 쌓여야한다.
		final int tryCount = 1000;

		// 최대 100개 동시요청
		final ExecutorService executorService = Executors.newFixedThreadPool(100);
		final CountDownLatch countDownLatch = new CountDownLatch(tryCount);

		// 1000번 동시 요청을 보낸다.
		// 각각 다른 사용자 ID로 보낸다
		for (int i = 0; i < tryCount; i++) {
			final long userId = i;
			executorService.execute(() -> {
				try {
					challengeLogSaveManager.saveLog(singleProblemId, userId,
						new SolveResult(true, Collections.emptySet(), Collections.emptyList()));
				} finally {
					countDownLatch.countDown();
				}
			});
		}

		countDownLatch.await();
		executorService.shutdown();

		Assertions.assertAll(
			// 로그는 1000개 모두 저장되어야 한다.
			() -> Assertions.assertEquals(tryCount, challengeLogRepository.findAllBySingleProblemId(singleProblemId).size()),
			// 같은 사용자의 "첫 시도 성공"은 단 한 번만 인정되어야 한다.
			// 결국 1000개로 저장되야 한다
			() -> Assertions.assertEquals(tryCount, singleProblemRepository.findById(singleProblemId).get().getFirstTrySuccessCount())
		);
	}

	@AfterEach
	void clear() {
		challengeLogRepository.deleteAll();
		singleProblemRepository.deleteAll();
	}
}