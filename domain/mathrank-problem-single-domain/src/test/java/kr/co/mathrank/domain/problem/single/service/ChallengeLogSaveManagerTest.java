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
import org.springframework.stereotype.Component;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

import kr.co.mathrank.client.internal.problem.SolveResult;
import kr.co.mathrank.domain.problem.single.entity.SingleProblem;
import kr.co.mathrank.domain.problem.single.repository.ChallengeLogRepository;
import kr.co.mathrank.domain.problem.single.repository.SingleProblemRepository;
import lombok.RequiredArgsConstructor;

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
	@Autowired
	private SaveLogBulkService saveLogBulkService;

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
	void 문제풀이_시도_기록은_모두_저장된다() throws InterruptedException {
		final Long singleProblemId = singleProblemRepository.save(SingleProblem.of(1L, 2L)).getId();

		final int tryCount = 100;

		// 최대 100개 동시요청
		final ExecutorService executorService = Executors.newFixedThreadPool(100);
		final CountDownLatch countDownLatch = new CountDownLatch(tryCount);

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

		// 문제풀이 기록은 모두 기록되야 한다.
		Assertions.assertEquals(tryCount, challengeLogRepository.findAllBySingleProblemId(singleProblemId).size());
	}

	@Test
	void 사용자별로_문제풀이_시도횟수를_증가시킨다() throws InterruptedException {
		final Long singleProblemId = singleProblemRepository.save(SingleProblem.of(1L, 2L)).getId();

		final int tryCount = 10;

		// 최대 10개 동시요청
		final ExecutorService executorService = Executors.newFixedThreadPool(10);
		final CountDownLatch countDownLatch = new CountDownLatch(tryCount);

		// 사용자는 총 2명
		final Long userId1 = 1L;
		final Long userId2 = 2L;

		for (int i = 0; i < tryCount; i++) {
			executorService.execute(() -> {
				try {
					challengeLogSaveManager.saveLog(singleProblemId, userId1,
						new SolveResult(true, Collections.emptySet(), Collections.emptyList()));
					challengeLogSaveManager.saveLog(singleProblemId, userId2,
						new SolveResult(true, Collections.emptySet(), Collections.emptyList()));
				} finally {
					countDownLatch.countDown();
				}
			});
		}

		countDownLatch.await();
		executorService.shutdown();

		// 개별 사용자는 총 두명이다
		Assertions.assertEquals(2,
			singleProblemRepository.findById(singleProblemId).get().getAttemptedUserDistinctCount());
	}

	@Test
	void 같은_사용자의_시도에도_총_시도횟수는_증가한다() throws InterruptedException {
		final Long singleProblemId = singleProblemRepository.save(SingleProblem.of(1L, 2L)).getId();

		// 로그는 총 1000개 쌓여야한다.
		final int tryCount = 10;

		// 최대 100개 동시요청
		final ExecutorService executorService = Executors.newFixedThreadPool(10);
		final CountDownLatch countDownLatch = new CountDownLatch(tryCount);

		final Long userId = 1L;

		// 1000번 동시 요청을 보낸다.
		for (int i = 0; i < tryCount; i++) {
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

		// 같은 사용자의 문제풀이 시도횟수도 기록된다.
		Assertions.assertEquals(tryCount,
			singleProblemRepository.findById(singleProblemId).get().getTotalAttemptedCount());
	}

	@Test
	void 첫_시도에_실패하면_이후의_시도에도_카운트되지_않는다() throws InterruptedException {
		final Long singleProblemId = singleProblemRepository.save(SingleProblem.of(1L, 2L)).getId();

		// 로그는 총 1000개 쌓여야한다.
		final int tryCount = 10;

		// 최대 100개 동시요청
		final ExecutorService executorService = Executors.newFixedThreadPool(10);
		final CountDownLatch countDownLatch = new CountDownLatch(tryCount);

		final Long userId = 1L;

		// 첫시도에서 실패
		challengeLogSaveManager.saveLog(singleProblemId, userId,
			new SolveResult(false, Collections.emptySet(), Collections.emptyList()));

		// 문제풀이 성공
		for (int i = 0; i < tryCount; i++) {
			executorService.execute(() -> {
				try {
					challengeLogSaveManager.saveLog(singleProblemId, userId,
						// 문제풀이 실패
						new SolveResult(false, Collections.emptySet(), Collections.emptyList()));
				} finally {
					countDownLatch.countDown();
				}
			});
		}

		countDownLatch.await();
		executorService.shutdown();

		// 첫시도에 성공한 카운트는 증가하지 않는다
		Assertions.assertEquals(0,
			singleProblemRepository.findById(singleProblemId).get().getFirstTrySuccessCount());
	}

	@Test
	void 같은_사용자의_첫시도_문제_풀이_성공_기록은_한번만_적용되야한다() throws InterruptedException {
		final Long singleProblemId = singleProblemRepository.save(SingleProblem.of(1L, 2L)).getId();
		final Long userId = 1L;

		final int tryCount = 10;

		// 최대 100개 동시요청
		final ExecutorService executorService = Executors.newFixedThreadPool(100);
		final CountDownLatch countDownLatch = new CountDownLatch(tryCount);

		for (int i = 0; i < tryCount; i++) {
			executorService.execute(() -> {
				try {
					// 같은 사용자의 성공 결과 저장
					challengeLogSaveManager.saveLog(singleProblemId, userId,
						new SolveResult(true, Collections.emptySet(), Collections.emptyList()));
				} finally {
					countDownLatch.countDown();
				}
			});
		}

		countDownLatch.await();
		executorService.shutdown();

		// 첫 시도에 성공한 카운트는 1이여야한다
		Assertions.assertEquals(1, singleProblemRepository.findById(singleProblemId).get().getFirstTrySuccessCount());
	}

	@Test
	void 외부_트랜잭션을_사용할때_mvcc_스냅샷_문제로_중복성공이_기록되지_않아야한다() throws InterruptedException {
		final Long singleProblemId = singleProblemRepository.save(SingleProblem.of(1L, 2L)).getId();

		final Long userId = 1L;

		final int tryCount = 10;

		// 최대 100개 동시요청
		final ExecutorService executorService = Executors.newFixedThreadPool(100);
		final CountDownLatch countDownLatch = new CountDownLatch(tryCount);

		for (int i = 0; i < tryCount; i++) {
			executorService.execute(() -> {
				try {
					// 같은 사용자의 성공 결과 저장
					saveLogBulkService.runSave(singleProblemId, userId); // 내부에서 saveLog 10번 호출
				} finally {
					countDownLatch.countDown();
				}
			});
		}

		countDownLatch.await();
		executorService.shutdown();

		// then
		final SingleProblem singleProblem = singleProblemRepository.findById(singleProblemId).get();

		Assertions.assertEquals(tryCount, singleProblem.getTotalAttemptedCount()); // 시도는 모두 기록됨
		Assertions.assertEquals(1, singleProblem.getFirstTrySuccessCount()); // 성공은 1번만 기록됨
	}

	@AfterEach
	void clear() {
		challengeLogRepository.deleteAll();
		singleProblemRepository.deleteAll();
	}
}

/**
 * 외부 트랜잭션에서 mvcc 스냅샷이 만들어짐을 테스트하기 위한 클래스
 */
@Component
@RequiredArgsConstructor
class SaveLogBulkService {
	private final ChallengeLogSaveManager challengeLogSaveManager;
	private final ChallengeLogRepository challengeLogRepository;

	@Transactional
	public void runSave(Long singleProblemId, Long userId) {
		// mvcc 스냅샷 생성
		challengeLogRepository.findAll();

		challengeLogSaveManager.saveLog(singleProblemId, userId,
			new SolveResult(true, Collections.emptySet(), Collections.emptyList()));
	}
}

