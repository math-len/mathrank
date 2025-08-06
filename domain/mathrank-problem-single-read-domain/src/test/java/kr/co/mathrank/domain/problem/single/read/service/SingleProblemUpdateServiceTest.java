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

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kr.co.mathrank.domain.problem.core.AnswerType;
import kr.co.mathrank.domain.problem.core.Difficulty;
import kr.co.mathrank.domain.problem.single.read.dto.SingleProblemReadModelUpdateCommand;
import kr.co.mathrank.domain.problem.single.read.entity.SingleProblemReadModel;
import kr.co.mathrank.domain.problem.single.read.repository.SingleProblemReadModelRepository;

@SpringBootTest(
	properties = {
		"spring.jpa.show-sql=true",
		"spring.jpa.hibernate.ddl-auto=create"
	}
)
@Testcontainers
@Transactional
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
	void command의_업데이트_시간이_최신일때만_업데이트한다() {
		final String beforePath = "beforePath";
		final String updatedPath = "updatedPath";
		final long problemId = 1L;
		final LocalDateTime beforeTime = LocalDateTime.now();
		final LocalDateTime updatedTime = beforeTime.plusDays(1L);

		final SingleProblemReadModel model = SingleProblemReadModel.of(problemId, problemId, "img", beforePath, null, Difficulty.MID,
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
	void command의_업데이트_시간이_이전이면_업데이트하지_않는다() {
		final String initialPath = "initialPath";
		final String outdatedPath = "outdatedPath";
		final long problemId = 2L;

		final LocalDateTime latestTime = LocalDateTime.now();
		final LocalDateTime outdatedTime = latestTime.minusDays(1L);

		final SingleProblemReadModel model = SingleProblemReadModel.of(
			problemId, problemId, "img", initialPath, AnswerType.SHORT_ANSWER, Difficulty.LOW,
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
	void command의_업데이트_시간이_동일하면_업데이트하지_않는다() {
		final String beforePath = "beforePath";
		final String updatedPath = "updatedPath";
		final long problemId = 3L;

		final LocalDateTime time = LocalDateTime.now();

		final SingleProblemReadModel model = SingleProblemReadModel.of(
			problemId, problemId, "img", beforePath, AnswerType.MULTIPLE_CHOICE, Difficulty.MID,
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

}