package kr.co.mathrank.domain.problem.single.read.entity;

import java.time.LocalDateTime;

import org.assertj.core.api.Assertions;
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
import kr.co.mathrank.domain.problem.single.read.repository.SingleProblemReadModelRepository;

@Transactional
@SpringBootTest
@Testcontainers
class SingleProblemReadModelTest {
	@Autowired
	private SingleProblemReadModelRepository repository;
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
	void 영속화_될때_정확도가_계산된다() {
		final SingleProblemReadModel readModel = SingleProblemReadModel.of(
			1L,
			2L,
			"singleProblemName",
			"testImage",
			"path",
			AnswerType.MULTIPLE_CHOICE,
			Difficulty.HIGH,
			100L,
			2000L,
			1000L,
			LocalDateTime.now()
		);

		repository.save(readModel);

		entityManager.flush();
		entityManager.clear();

		// (100L / 1000L) * 100 임으로, 10.00 나와야함
		Assertions.assertThat(readModel.getAccuracy()).isEqualTo(Double.valueOf(10.00d));
	}

	@Test
	void 업데이트_될때_정확도가_계산된다() {
		final SingleProblemReadModel readModel = SingleProblemReadModel.of(
			1L,
			2L,
			"singleProblemName",
			"testImage",
			"path",
			AnswerType.MULTIPLE_CHOICE,
			Difficulty.HIGH,
			100L,
			2000L,
			1000L,
			LocalDateTime.now()
		);
		repository.save(readModel);

		entityManager.flush();
		entityManager.clear();

		// 가져오기
		final SingleProblemReadModel target = repository.findById(readModel.getId()).get();
		target.setAttemptedUserDistinctCount(2000L);

		entityManager.flush();
		entityManager.clear();

		// (100L / 2000L) * 100 임으로, 5.00 나와야함
		Assertions.assertThat(target.getAccuracy()).isEqualTo(Double.valueOf(5.00d));
	}
}
