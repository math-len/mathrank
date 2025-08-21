package kr.co.mathrank.domain.problem.single.service;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

import kr.co.mathrank.client.internal.problem.SolveResult;
import kr.co.mathrank.domain.problem.single.dto.SingleProblemSolveCommand;
import kr.co.mathrank.domain.problem.single.entity.SingleProblem;
import kr.co.mathrank.domain.problem.single.repository.SingleProblemRepository;

@SpringBootTest
class ChallengerQueryServiceTest {
	@Autowired
	private SingleProblemRepository singleProblemRepository;
	@Autowired
	private SingleProblemService singleProblemService;
	@Autowired
	private ChallengerQueryService challengerQueryService;

	@MockitoBean
	private ProblemInfoManager problemInfoManager;

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
	@Transactional
	void 사용자가_푼_모든_문제_조회() {
		Mockito.when(problemInfoManager.solve(Mockito.anyLong(), Mockito.anyList()))
			.thenReturn(new SolveResult(true, Collections.emptySet(), Collections.emptyList()));

		final Long memberId = 1L;
		for (int i = 0; i < 10; i ++) {
			final Long singleProblemId = singleProblemRepository.save(SingleProblem.of((long) i , "test", 2L)).getId();
			singleProblemService.solve(new SingleProblemSolveCommand(singleProblemId, memberId, List.of("test")));
		}

		Assertions.assertEquals(10, challengerQueryService.findMemberChallenges(memberId).results().size());
	}

	@Test
	void 여러_사용자가_푼것중_내것만_조회된다() {
		Mockito.when(problemInfoManager.solve(Mockito.anyLong(), Mockito.anyList()))
			.thenReturn(new SolveResult(true, Collections.emptySet(), Collections.emptyList()));

		// 0번 부터 10번까지 사용자로 문제 풀기
		for (int i = 0; i < 10; i ++) {
			final Long singleProblemId = singleProblemRepository.save(SingleProblem.of((long) i , "test", 2L)).getId();
			singleProblemService.solve(new SingleProblemSolveCommand(singleProblemId, (long) i, List.of("test")));
		}

		Assertions.assertEquals(1, challengerQueryService.findMemberChallenges(0L).results().size());
		Assertions.assertTrue(challengerQueryService.findMemberChallenges(0L).results().getFirst().success());
	}
}
