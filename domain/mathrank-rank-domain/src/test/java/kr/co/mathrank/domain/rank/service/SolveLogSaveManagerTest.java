package kr.co.mathrank.domain.rank.service;

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
import org.testcontainers.junit.jupiter.Testcontainers;

import kr.co.mathrank.domain.rank.dto.SolveLogRegisterCommand;
import kr.co.mathrank.domain.rank.entity.Solver;
import kr.co.mathrank.domain.rank.exception.SolveLogAlreadyRegisteredException;
import kr.co.mathrank.domain.rank.repository.SolverRepository;

@SpringBootTest
@Testcontainers
class SolveLogSaveManagerTest {
	@Autowired
	private SolveLogSaveManager solveLogSaveManager;
	@Autowired
	private SolverRepository solverRepository;

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

	/**
	 * 자식 엔티티를 포함해서 persist할 때, 자식엔티티 영속화 과정에서 DataIntegrityException이 발생했을떄, 롤백되지 않는 상황이 있었음
	 */
	@Test
	void 유니크_제약_조건_위반시_롤백() {
		final Long memberId = 1L;
		final int score = 100;
		solveLogSaveManager.save(new SolveLogRegisterCommand(1L, 2L, memberId, true), score);
		Assertions.assertThrows(SolveLogAlreadyRegisteredException.class, () ->
			solveLogSaveManager.save(new SolveLogRegisterCommand(1L, 2L, memberId, true), score));

		final Solver solver = solverRepository.findByMemberId(memberId).get();
		Assertions.assertEquals(1, solverRepository.count());
		Assertions.assertEquals(1, solver.getSolveLogs().size());
		Assertions.assertEquals(score, solver.getScore());
	}

	@Test
	void 동시성환경에서도_정상_저장_및_계산() throws InterruptedException {
		final Long memberId = 1L;
		final int tryCount = 10;
		final int score = 100;

		final ExecutorService executorService = Executors.newFixedThreadPool(5);
		final CountDownLatch countDownLatch = new CountDownLatch(10);

		for (int i = 0; i < tryCount; i++) {
			executorService.submit(() -> {
				try {
					solveLogSaveManager.save(new SolveLogRegisterCommand(1L, 2L, memberId, true), score);
				} finally {
					countDownLatch.countDown();
				}
			});
		}

		countDownLatch.await();

		final Solver solver = solverRepository.findByMemberId(memberId).get();
		Assertions.assertEquals(1, solverRepository.count());
		Assertions.assertEquals(1, solver.getSolveLogs().size());
		Assertions.assertEquals(score, solver.getScore());
	}

	@AfterEach
	void clear() {
		solverRepository.deleteAll();
	}
}