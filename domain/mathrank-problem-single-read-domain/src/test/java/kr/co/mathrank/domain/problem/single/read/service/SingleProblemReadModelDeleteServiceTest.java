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
import kr.co.mathrank.domain.problem.core.Difficulty;
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
class SingleProblemReadModelDeleteServiceTest {
	@Autowired
	private SingleProblemReadModelDeleteService singleProblemReadModelDeleteService;
	@Autowired
	private SingleProblemReadModelRepository singleProblemReadModelRepository;
	@Autowired
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
	void problemId로_삭제된다() {
		final Long problemId = 1L;
		final Long singleProblemId = 2L;
		final LocalDateTime baseTime = LocalDateTime.of(2018, 1, 1, 1, 1);
		final SingleProblemReadModel model = SingleProblemReadModel.of(singleProblemId, problemId, "img", "initialPath",
			null, Difficulty.MID,
			300L, 2000L, 1000L, baseTime);

		singleProblemReadModelRepository.save(model);

		entityManager.flush();
		entityManager.clear();

		singleProblemReadModelDeleteService.deleteByProblemId(problemId);

		Assertions.assertEquals(0, singleProblemReadModelRepository.count());
	}

	@Test
	void 일치하는_problemId_없어도_에러처리_안해() {
		final Long problemId = 1L;
		final Long singleProblemId = 2L;

		// 존재 안하는애로 삭제
		final Long notExistProblemId = 100L;

		final LocalDateTime baseTime = LocalDateTime.of(2018, 1, 1, 1, 1);
		final SingleProblemReadModel model = SingleProblemReadModel.of(singleProblemId, problemId, "img", "initialPath",
			null, Difficulty.MID,
			300L, 2000L, 1000L, baseTime);

		singleProblemReadModelRepository.save(model);

		entityManager.flush();
		entityManager.clear();

		// 존재 안하는 놈 삭제 시도
		singleProblemReadModelDeleteService.deleteByProblemId(notExistProblemId);

		Assertions.assertEquals(1, singleProblemReadModelRepository.count());
	}

	@Test
	void singleProblemId로_삭제된다() {
		final Long problemId = 1L;
		final Long singleProblemId = 2L;
		final LocalDateTime baseTime = LocalDateTime.of(2018, 1, 1, 1, 1);
		final SingleProblemReadModel model = SingleProblemReadModel.of(singleProblemId, problemId, "img", "initialPath",
			null, Difficulty.MID,
			300L, 2000L, 1000L, baseTime);

		singleProblemReadModelRepository.save(model);

		entityManager.flush();
		entityManager.clear();

		singleProblemReadModelDeleteService.deleteBySingleProblemId(singleProblemId);

		Assertions.assertEquals(0, singleProblemReadModelRepository.count());
	}

	@Test
	void 일치하는_singleProblemId_없어도_에러처리_안해() {
		final Long problemId = 1L;
		final Long singleProblemId = 2L;
		final Long notExistSingleProblemId = 100L;
		final LocalDateTime baseTime = LocalDateTime.of(2018, 1, 1, 1, 1);
		final SingleProblemReadModel model = SingleProblemReadModel.of(singleProblemId, problemId, "img", "initialPath",
			null, Difficulty.MID,
			300L, 2000L, 1000L, baseTime);

		singleProblemReadModelRepository.save(model);

		entityManager.flush();
		entityManager.clear();

		singleProblemReadModelDeleteService.deleteBySingleProblemId(notExistSingleProblemId);

		Assertions.assertEquals(1, singleProblemReadModelRepository.count());
	}
}
