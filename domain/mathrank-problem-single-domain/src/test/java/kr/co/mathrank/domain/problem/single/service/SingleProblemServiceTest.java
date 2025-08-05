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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

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
		spring.jpa.hibernate.ddl-auto=create
		"""
)
class SingleProblemServiceTest {
	@Autowired
	private SingleProblemService singleProblemService;
	@Autowired
	private ChallengeLogRepository challengeLogRepository;
	@Autowired
	private SingleProblemRepository singleProblemRepository;

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

	@BeforeEach
	void clear() {
		singleProblemRepository.deleteAll();
		challengeLogRepository.deleteAll();
	}
}
