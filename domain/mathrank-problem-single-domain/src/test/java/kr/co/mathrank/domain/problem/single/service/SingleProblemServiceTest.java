package kr.co.mathrank.domain.problem.single.service;

import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.ConstraintViolationException;
import kr.co.mathrank.client.internal.problem.ProblemClient;
import kr.co.mathrank.client.internal.problem.SolveResult;
import kr.co.mathrank.common.role.Role;
import kr.co.mathrank.domain.problem.single.dto.SingleProblemRegisterCommand;
import kr.co.mathrank.domain.problem.single.dto.SingleProblemSolveCommand;
import kr.co.mathrank.domain.problem.single.exception.AlreadyRegisteredProblemException;
import kr.co.mathrank.domain.problem.single.exception.CannotRegisterWithThisRoleException;
import kr.co.mathrank.domain.problem.single.repository.ChallengeLogRepository;
import kr.co.mathrank.domain.problem.single.repository.SingleProblemRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest(
	properties = """
		snowflake.node.id=111
		spring.jpa.show-sql=true
		spring.jpa.properties.hibernate.format_sql=true
		"""
)
class SingleProblemServiceTest {
	@Autowired
	private SingleProblemService singleProblemService;
	@MockitoBean
	private ProblemClient problemClient;
	@Autowired
	private ChallengeLogRepository challengeLogRepository;
	@Autowired
	private SingleProblemRepository singleProblemRepository;

	@Test
	void 어드민만_문제를_개별문제로_등록할_수_있다() {
		Assertions.assertDoesNotThrow(() -> singleProblemService.register(new SingleProblemRegisterCommand(1L, 2L, Role.ADMIN)));
	}

	@Test
	void 일반사용자는_개별문제_등록을_할_수_없다() {
		Assertions.assertThrows(CannotRegisterWithThisRoleException.class, () -> singleProblemService.register(new SingleProblemRegisterCommand(1L, 2L, Role.USER)));
	}

	@Test
	@Transactional
	void 이미_등록된_문제는_다시_등록할_수_없다() {
		final Long problemId = 1L;

		singleProblemService.register(new SingleProblemRegisterCommand(problemId, 2L, Role.ADMIN));
		Assertions.assertThrows(AlreadyRegisteredProblemException.class, () -> singleProblemService.register(new SingleProblemRegisterCommand(problemId, 2L, Role.ADMIN)));
	}

	@Test
	void 형식_검증() {

		Assertions.assertAll(
			() -> Assertions.assertThrows(ConstraintViolationException.class, () -> singleProblemService.register(null)),
			() -> Assertions.assertThrows(ConstraintViolationException.class, () -> singleProblemService.register(new SingleProblemRegisterCommand(null, 2L, Role.ADMIN))),
			() -> Assertions.assertThrows(ConstraintViolationException.class, () -> singleProblemService.register(new SingleProblemRegisterCommand(1L, null, Role.ADMIN))),
			() -> Assertions.assertThrows(ConstraintViolationException.class, () -> singleProblemService.register(new SingleProblemRegisterCommand(1L, 2L, null)))
		);
	}

	@Test
	void 문제풀이결과_기록은_동시성_문제가_없다() throws InterruptedException {
		long memberId = 2L;

		// 풀 문제를 등록한다
		final Long singleProblemId = singleProblemService.register(
			new SingleProblemRegisterCommand(1L, memberId, Role.ADMIN));

		// 채점 결과는 항상 true 응답
		Mockito.when(problemClient.matchAnswer(Mockito.anyLong(), Mockito.anyList()))
			.thenReturn(new SolveResult(true, Collections.emptySet(), Collections.emptyList()));

		final int tryCount = 1000; // 로그는 총 1000개 쌓여야한다.

		// 최대 100개 동시요청
		final ExecutorService executorService = Executors.newFixedThreadPool(100);
		final CountDownLatch countDownLatch = new CountDownLatch(tryCount);

		// 동시요청 시작
		for (int i = 0; i < tryCount; i++) {
			executorService.execute(() -> {
				singleProblemService.solve(
					new SingleProblemSolveCommand(singleProblemId, memberId, Collections.emptyList()));
				countDownLatch.countDown();
			});
		}

		countDownLatch.await();
		executorService.shutdown();

		Assertions.assertEquals(tryCount, challengeLogRepository.findAll().size());
	}

	@BeforeEach
	void clear() {
		singleProblemRepository.deleteAll();
		challengeLogRepository.deleteAll();
	}
}
