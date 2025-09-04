package kr.co.mathrank.domain.single.problem.rank.service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.co.mathrank.domain.single.problem.rank.dto.SolveLogRegisterCommand;
import kr.co.mathrank.domain.single.problem.rank.entity.Solver;
import kr.co.mathrank.domain.single.problem.rank.repository.SolverRepository;

@SpringBootTest
class SolveLogSaveManagerTest {
	@Autowired
	private SolveLogSaveManager solveLogSaveManager;
	@Autowired
	private SolverRepository solverRepository;

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
		Assertions.assertEquals(score * tryCount, solver.getScore());
	}
}