package kr.co.mathrank.domain.problem.single.read.service;

import java.time.LocalDateTime;

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

import kr.co.mathrank.domain.problem.core.AnswerType;
import kr.co.mathrank.domain.problem.core.Difficulty;
import kr.co.mathrank.domain.problem.single.read.dto.SingleProblemReadModelRegisterCommand;
import kr.co.mathrank.domain.problem.single.read.entity.SingleProblemReadModel;
import kr.co.mathrank.domain.problem.single.read.exception.SingleProblemReadModelAlreadyExistException;

@SpringBootTest(
	properties = {
		"spring.jpa.show-sql=true",
		"spring.jpa.hibernate.ddl-auto=create"
	}
)
@Testcontainers
@Transactional
class SingleProblemReadModelRegisterServiceTest {
	@Autowired
	private SingleProblemReadModelRegisterService singleProblemReadModelRegisterService;

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
	void 중복삽입시_에러발생() {
		final Long problemId = 1L;
		final LocalDateTime baseTime = LocalDateTime.of(2018, 1, 1, 1, 1);

		singleProblemReadModelRegisterService.save(new SingleProblemReadModelRegisterCommand(
			problemId, problemId, "img", AnswerType.SHORT_ANSWER, Difficulty.LOW, "initialPath", baseTime
		));

		Assertions.assertThrows(SingleProblemReadModelAlreadyExistException.class,
			() -> singleProblemReadModelRegisterService.save(new SingleProblemReadModelRegisterCommand(
				problemId, problemId, "img", AnswerType.SHORT_ANSWER, Difficulty.LOW, "initialPath", baseTime
			)));
	}
}
