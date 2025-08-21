package kr.co.mathrank.domain.problem.single.read.service;

import java.time.LocalDateTime;
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
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kr.co.mathrank.domain.problem.core.AnswerType;
import kr.co.mathrank.domain.problem.core.Difficulty;
import kr.co.mathrank.domain.problem.single.read.dto.SingleProblemAttemptStatsUpdateCommand;
import kr.co.mathrank.domain.problem.single.read.dto.SingleProblemReadModelUpdateCommand;
import kr.co.mathrank.domain.problem.single.read.entity.SingleProblemReadModel;
import kr.co.mathrank.domain.problem.single.read.exception.CannotFoundProblemException;
import kr.co.mathrank.domain.problem.single.read.repository.SingleProblemReadModelRepository;

@SpringBootTest(
	properties = {
		"spring.jpa.show-sql=true",
		"spring.jpa.hibernate.ddl-auto=create"
	}
)
@Testcontainers
class SingleProblemUpdateServiceTest {
	@Autowired
	private SingleProblemUpdateService singleProblemUpdateService;
	@Autowired
	private SingleProblemReadModelRepository singleProblemReadModelRepository;
	@PersistenceContext
	private EntityManager entityManager;

	@Container
	static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0.42")
		.withDatabaseName("testdb")
		.withUsername("user")
		.withPassword("password");

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", mysql::getJdbcUrl);
		registry.add("spring.datasource.username", mysql::getUsername);
		registry.add("spring.datasource.password", mysql::getPassword);
	}

	@Test
	@Transactional
	void command의_업데이트_시간이_최신일때만_업데이트한다() {
		final String beforePath = "beforePath";
		final String updatedPath = "updatedPath";
		final long problemId = 1L;
		final LocalDateTime beforeTime = LocalDateTime.now();
		final LocalDateTime updatedTime = beforeTime.plusDays(1L);

		final SingleProblemReadModel model = SingleProblemReadModel.of(problemId, problemId, "singleProblemName", "img", beforePath, null, Difficulty.MID,
			300L, 2000L, 1000L, beforeTime);

		singleProblemReadModelRepository.save(model);

		entityManager.flush();
		entityManager.clear();

		// path를 updatedPath
		singleProblemUpdateService.updateProblemInfo(new SingleProblemReadModelUpdateCommand(
			problemId, updatedPath, "null", AnswerType.MULTIPLE_CHOICE, Difficulty.KILLER, updatedTime
		));

		entityManager.flush();
		entityManager.clear();

		// udpatedTime 이면 반영한다.
		final SingleProblemReadModel updatedModel = singleProblemReadModelRepository.findByProblemIdForUpdate(
			problemId).orElseThrow();
		Assertions.assertEquals(updatedPath, updatedModel.getCoursePath());
	}

	@Test
	@Transactional
	void command의_업데이트_시간이_이전이면_업데이트하지_않는다() {
		final String initialPath = "initialPath";
		final String outdatedPath = "outdatedPath";
		final long problemId = 2L;

		final LocalDateTime latestTime = LocalDateTime.of(2020, 1, 1, 0, 0);
		final LocalDateTime outdatedTime = latestTime.minusDays(1L);

		final SingleProblemReadModel model = SingleProblemReadModel.of(
			problemId, problemId, "singleProblemName", "img", initialPath, AnswerType.SHORT_ANSWER, Difficulty.LOW,
			300L, 2000L, 1000L, latestTime
		);
		singleProblemReadModelRepository.save(model);

		entityManager.flush();
		entityManager.clear();

		// outdatedTime으로 업데이트 시도
		singleProblemUpdateService.updateProblemInfo(new SingleProblemReadModelUpdateCommand(
			problemId, outdatedPath, "img", AnswerType.MULTIPLE_CHOICE, Difficulty.KILLER, outdatedTime
		));

		entityManager.flush();
		entityManager.clear();

		// 업데이트되지 않아야 한다
		final SingleProblemReadModel loaded = singleProblemReadModelRepository.findByProblemIdForUpdate(problemId).orElseThrow();
		Assertions.assertEquals(initialPath, loaded.getCoursePath());
		Assertions.assertEquals(Difficulty.LOW, loaded.getDifficulty());
		Assertions.assertEquals(AnswerType.SHORT_ANSWER, loaded.getAnswerType());
	}

	@Test
	@Transactional
	void command의_업데이트_시간이_동일하면_업데이트하지_않는다() {
		final String beforePath = "beforePath";
		final String updatedPath = "updatedPath";
		final long problemId = 3L;

		final LocalDateTime time = LocalDateTime.of(2020, 1, 1, 0, 0);

		final SingleProblemReadModel model = SingleProblemReadModel.of(
			problemId, problemId, "singleProblemName", "img", beforePath, AnswerType.MULTIPLE_CHOICE, Difficulty.MID,
			300L, 2000L, 1000L, time
		);
		singleProblemReadModelRepository.save(model);

		entityManager.flush();
		entityManager.clear();

		// 동일한 시간으로 업데이트 시도
		singleProblemUpdateService.updateProblemInfo(new SingleProblemReadModelUpdateCommand(
			problemId, updatedPath, "img", AnswerType.SHORT_ANSWER, Difficulty.KILLER, time
		));

		entityManager.flush();
		entityManager.clear();

		// 업데이트되지 않아야 한다
		final SingleProblemReadModel loaded = singleProblemReadModelRepository.findByProblemIdForUpdate(problemId).orElseThrow();
		Assertions.assertEquals(beforePath, loaded.getCoursePath());
		Assertions.assertEquals(Difficulty.MID, loaded.getDifficulty());
		Assertions.assertEquals(AnswerType.MULTIPLE_CHOICE, loaded.getAnswerType());
	}

	@Test
	@Transactional
	void command의_totalAttemptedCount가_기존보다_클때_업데이트된다() {
		// given
		final long problemId = 10L;
		final LocalDateTime time = LocalDateTime.of(2020, 1, 1, 0, 0);
		final SingleProblemReadModel model = SingleProblemReadModel.of(
			problemId, problemId, "singleProblemName", "img", "coursePath", AnswerType.SHORT_ANSWER, Difficulty.LOW,
			1L, 5L, 10L, time
		);
		singleProblemReadModelRepository.save(model);

		entityManager.flush();
		entityManager.clear();

		// when
		singleProblemUpdateService.updateAttemptStatistics(new SingleProblemAttemptStatsUpdateCommand(
			problemId, 10L, true, 2L, 6L, 11L
		));

		entityManager.flush();
		entityManager.clear();

		// then
		final SingleProblemReadModel updated = singleProblemReadModelRepository.findById(problemId).orElseThrow();
		Assertions.assertEquals(2L, updated.getFirstTrySuccessCount());
		Assertions.assertEquals(6L, updated.getTotalAttemptedCount());
		Assertions.assertEquals(11L, updated.getAttemptedUserDistinctCount());
	}

	@Test
	@Transactional
	void command의_totalAttemptedCount가_기존값이하이면_업데이트되지_않는다() {
		// given
		final long problemId = 11L;
		final LocalDateTime time = LocalDateTime.of(2020, 1, 1, 0, 0);
		final SingleProblemReadModel model = SingleProblemReadModel.of(
			problemId, problemId, "singleProblemName", "img", "coursePath", AnswerType.SHORT_ANSWER, Difficulty.LOW,
			3L, 6L, 10L, time
		);
		singleProblemReadModelRepository.save(model);

		entityManager.flush();
		entityManager.clear();

		// when
		singleProblemUpdateService.updateAttemptStatistics(new SingleProblemAttemptStatsUpdateCommand(
			problemId, 10L, true, 999L, 6L, 10L
		));

		entityManager.flush();
		entityManager.clear();

		// then
		final SingleProblemReadModel updated = singleProblemReadModelRepository.findById(problemId).orElseThrow();
		Assertions.assertEquals(3L, updated.getFirstTrySuccessCount());
		Assertions.assertEquals(6L, updated.getTotalAttemptedCount());
		Assertions.assertEquals(10L, updated.getAttemptedUserDistinctCount());
	}

	@Test
	@Transactional
	void 존재하지_않는_problemId이면_예외를_던진다() {
		// given
		final long nonexistentId = 9999L;

		// expect
		Assertions.assertThrows(CannotFoundProblemException.class, () -> {
			singleProblemUpdateService.updateAttemptStatistics(new SingleProblemAttemptStatsUpdateCommand(
				nonexistentId, 10L, true, 1L, 2L, 3L
			));
		});
	}

	@Test
	void 문제정보와_통계정보가_여러_스레드에서_경합해도_최신값이_반영된다() throws InterruptedException {
		// given
		final long problemId = 200L;
		final LocalDateTime baseTime = LocalDateTime.of(2020, 1, 1, 0, 0);

		final SingleProblemReadModel model = SingleProblemReadModel.of(
			problemId, problemId, "singleProblemName", "img", "initialPath", AnswerType.SHORT_ANSWER, Difficulty.LOW,
			0L, 0L, 0L, baseTime
		);
		singleProblemReadModelRepository.save(model);

		final int threadCount = 20;
		final CountDownLatch latch = new CountDownLatch(threadCount);
		final ExecutorService executor = Executors.newFixedThreadPool(10);

		// when
		for (int i = 1; i <= threadCount; i++) {
			final int index = i;

			executor.submit(() -> {
				try {
					if (index % 2 == 0) {
						// 통계 업데이트: totalAttemptedCount 증가
						long attemptCount = index * 10L;
						singleProblemUpdateService.updateAttemptStatistics(
							new SingleProblemAttemptStatsUpdateCommand(
								problemId,
								10L,
								true,
								attemptCount / 10,        // firstTrySuccessCount
								attemptCount,         // totalCount
								attemptCount / 5              // attemptedDistinctCount
							)
						);
					} else {
						// 문제정보 업데이트: updatedAt 증가
						LocalDateTime updatedAt = baseTime.plusDays(index); // 점점 최신 시간
						singleProblemUpdateService.updateProblemInfo(
							new SingleProblemReadModelUpdateCommand(
								problemId,
								"path_" + index,
								"img_" + index,
								AnswerType.MULTIPLE_CHOICE,
								Difficulty.KILLER,
								updatedAt
							)
						);
					}
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();
		executor.shutdown();

		// then
		final SingleProblemReadModel updated = singleProblemReadModelRepository.findById(problemId).orElseThrow();

		// updatedAt 기준 최신 정보는 index=19 (가장 마지막 홀수)
		Assertions.assertEquals("path_19", updated.getCoursePath());
		Assertions.assertEquals("img_19", updated.getProblemImage());
		Assertions.assertEquals(Difficulty.KILLER, updated.getDifficulty());
		Assertions.assertEquals(AnswerType.MULTIPLE_CHOICE, updated.getAnswerType());
		Assertions.assertEquals(baseTime.plusDays(19), updated.getUpdatedAt());

		// totalAttemptedCount 기준 최댓값은 index=20 → 200
		Assertions.assertEquals(20L, updated.getFirstTrySuccessCount());
		Assertions.assertEquals(40L, updated.getAttemptedUserDistinctCount());
		Assertions.assertEquals(200L, updated.getTotalAttemptedCount());
	}

	@AfterEach
	void clear() {
		singleProblemReadModelRepository.deleteAll();
	}
}
