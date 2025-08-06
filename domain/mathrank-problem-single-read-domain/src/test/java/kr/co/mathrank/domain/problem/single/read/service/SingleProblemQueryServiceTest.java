package kr.co.mathrank.domain.problem.single.read.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

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

import jakarta.validation.ConstraintViolationException;
import kr.co.mathrank.domain.problem.core.Difficulty;
import kr.co.mathrank.domain.problem.single.read.dto.SingleProblemReadModelPageResult;
import kr.co.mathrank.domain.problem.single.read.dto.SingleProblemReadModelQuery;
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
class SingleProblemQueryServiceTest {

	@Autowired
	private SingleProblemQueryService queryService;

	@Autowired
	private SingleProblemReadModelRepository repository;

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
	void 오프셋이_20_000초과로_조회할_수_없다() {
		final SingleProblemReadModelQuery query = new SingleProblemReadModelQuery(null, "math", null, Difficulty.MID, Difficulty.MID, 10, 30, null, null);

		// 20000이상임으로 불가
		Assertions.assertThrows(ConstraintViolationException.class, () -> queryService.queryPage(query, 20, 1000));
	}

	@Test
	void 정답률과_난이도조건으로_페이징_조회한다() {
		// Given
		repository.saveAll(List.of(
			// 정답률 30
			SingleProblemReadModel.of(1L, 1L, "img", "math", null, Difficulty.MID, 300L, 2000L, 1000L),
			// 정답률 10
			SingleProblemReadModel.of(2L, 2L, "img", "math", null, Difficulty.MID, 100L, 2000L, 1000L),
			// 정답률 20
			SingleProblemReadModel.of(3L, 3L, "img", "math", null, Difficulty.MID, 200L, 2000L, 1000L)
		));

		final SingleProblemReadModelQuery query = new SingleProblemReadModelQuery(
			null,
			"math",
			null,
			Difficulty.MID,
			Difficulty.MID,
			10,
			30,
			null,
			null
		);

		// When
		final SingleProblemReadModelPageResult result = queryService.queryPage(query, 2, 1);

		// Then
		// 두개가 나와야 한다
		Assertions.assertEquals(2, result.queryResults().size()); // 페이지당 2개
		Assertions.assertEquals(1, result.currentPageNumber()); // 페이지 번호
		Assertions.assertEquals(2, result.currentPageSize()); // 페이지 크기
		Assertions.assertEquals(1, result.possibleNextPageNumbers().size()); // 다음 페이지가 1개 존재한다

		// 다음 페이지도 테스트
		final SingleProblemReadModelPageResult nextResult = queryService.queryPage(query, 2, 2);

		// 다음 페이지에 1개가존재해야한다
		Assertions.assertEquals(1, nextResult.queryResults().size());

		// 다음 페이지는 없다
		Assertions.assertTrue(nextResult.possibleNextPageNumbers().isEmpty());
	}
}
