package kr.co.mathrank.domain.problem.single.read.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
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
import kr.co.mathrank.domain.problem.single.read.dto.SingleProblemAttemptStatsUpdateCommand;
import kr.co.mathrank.domain.problem.single.read.dto.SingleProblemReadModelPageResult;
import kr.co.mathrank.domain.problem.single.read.dto.SingleProblemReadModelQuery;
import kr.co.mathrank.domain.problem.single.read.dto.SingleProblemReadModelResult;
import kr.co.mathrank.domain.problem.single.read.entity.SingleProblemReadModel;
import kr.co.mathrank.domain.problem.single.read.repository.SingleProblemReadModelRepository;

@SpringBootTest(
	properties = {
		"spring.jpa.show-sql=true",
		"spring.jpa.hibernate.ddl-auto=create"
	}
)
@Testcontainers
class SingleProblemQueryServiceTest {

	@Autowired
	private SingleProblemQueryService queryService;
	@Autowired
	private SingleProblemUpdateService singleProblemUpdateService;

	@Autowired
	private SingleProblemReadModelRepository repository;

	@Container
	static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0.42")
		.withDatabaseName("testdb")
		.withUsername("user")
		.withPassword("password");
	@Autowired
	private SingleProblemReadModelRepository singleProblemReadModelRepository;

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", mysql::getJdbcUrl);
		registry.add("spring.datasource.username", mysql::getUsername);
		registry.add("spring.datasource.password", mysql::getPassword);
	}

	@Test
	void 단일_조회시_풀이성공여부가_필드예_포함된다() {
		final Long memberId = 10L;

		repository.saveAll(List.of(
			SingleProblemReadModel.of(1L, 1L, "singleProblemName", "img", "math", null, Difficulty.MID, 0L, 0L, 0L,
				LocalDateTime.now()),
			SingleProblemReadModel.of(2L, 2L, "singleProblemName", "img", "math", null, Difficulty.MID, 0L, 0L, 0L,
				LocalDateTime.now()),
			SingleProblemReadModel.of(3L, 3L, "singleProblemName", "img", "math", null, Difficulty.MID, 0L, 0L, 0L,
				LocalDateTime.now())
		));

		// 1번 문제는 첫시도에풀이 성공 표시
		singleProblemUpdateService.updateAttemptStatistics(new SingleProblemAttemptStatsUpdateCommand(
			1L, memberId, true, 1L, 1L, 1L
		));
		// 2번 문제는 첫시도에 풀이 실패 표시
		singleProblemUpdateService.updateAttemptStatistics(new SingleProblemAttemptStatsUpdateCommand(
			2L, memberId, false, 1L, 1L, 1L
		));
		// 3번은 풀이 시도 X

		Assertions.assertAll(
			() -> Assertions.assertTrue(queryService.getProblemWithSolverStatus(1L, memberId).successAtFirstTry()),
			() -> Assertions.assertFalse(queryService.getProblemWithSolverStatus(2L, memberId).successAtFirstTry()),
			() -> Assertions.assertNull(queryService.getProblemWithSolverStatus(3L, memberId).successAtFirstTry())
		);
	}

	@Test
	void 내가_푼_문제가_표시된다() {
		final Long memberId = 10L;

		repository.saveAll(List.of(
			SingleProblemReadModel.of(1L, 1L, "singleProblemName","img", "math", null, Difficulty.MID, 0L, 0L, 0L, LocalDateTime.now()),
			SingleProblemReadModel.of(2L, 2L, "singleProblemName","img", "math", null, Difficulty.MID, 0L, 0L, 0L, LocalDateTime.now()),
			SingleProblemReadModel.of(3L, 3L, "singleProblemName","img", "math", null, Difficulty.MID, 0L, 0L, 0L, LocalDateTime.now())
		));

		// 1번 문제는 첫시도에풀이 성공 표시
		singleProblemUpdateService.updateAttemptStatistics(new SingleProblemAttemptStatsUpdateCommand(
			1L, memberId, true, 1L, 1L, 1L
		));
		// 2번 문제는 첫시도에 풀이 실패 표시
		singleProblemUpdateService.updateAttemptStatistics(new SingleProblemAttemptStatsUpdateCommand(
			2L, memberId, false, 1L, 1L, 1L
		));
		// 3번은 풀이 시도 X

		final SingleProblemReadModelQuery query = new SingleProblemReadModelQuery(
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null
		);
		final SingleProblemReadModelPageResult result = queryService.queryPage(query, null, null, memberId, 10, 1);
		final List<SingleProblemReadModelResult> results = result.queryResults();

		// 1번 문제부터 정렬
		final List<SingleProblemReadModelResult> sorted = results.stream()
			.sorted(Comparator.comparing(SingleProblemReadModelResult::problemId))
			.toList();

		Assertions.assertAll(
			() -> Assertions.assertEquals(3, sorted.size()),
			() -> Assertions.assertTrue(sorted.get(0).successAtFirstTry()),
			() -> Assertions.assertFalse(sorted.get(1).successAtFirstTry()),
			() -> Assertions.assertNull(sorted.get(2).successAtFirstTry())
		);
	}

	@Test
	@Transactional
	void 오프셋이_20_000초과로_조회할_수_없다() {
		final SingleProblemReadModelQuery query = new SingleProblemReadModelQuery(null, "singleProblemName", "math", null, Difficulty.MID, Difficulty.MID, 10, 30, null, null);
		final Long memberId = 10L;

		Assertions.assertThrows(ConstraintViolationException.class, () -> queryService.queryPage(query, null, null, memberId, 20, 1001));
		Assertions.assertThrows(ConstraintViolationException.class, () -> queryService.queryPage(query, null, null, memberId, 21, 1000));
	}

	@Test
	@Transactional
	void 정답률과_난이도조건으로_페이징_조회한다() {
		// Given
		repository.saveAll(List.of(
			// 정답률 30
			SingleProblemReadModel.of(1L, 1L, "singleProblemName","img", "math", null, Difficulty.MID, 300L, 2000L, 1000L, LocalDateTime.now()),
			// 정답률 10
			SingleProblemReadModel.of(2L, 2L, "singleProblemName","img", "math", null, Difficulty.MID, 100L, 2000L, 1000L, LocalDateTime.now()),
			// 정답률 20
			SingleProblemReadModel.of(3L, 3L, "singleProblemName","img", "math", null, Difficulty.MID, 200L, 2000L, 1000L, LocalDateTime.now())
		));

		final SingleProblemReadModelQuery query = new SingleProblemReadModelQuery(
			null,
			"singleProblemName",
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
		final SingleProblemReadModelPageResult result = queryService.queryPage(query,null, null,  1000L, 2, 1);

		// Then
		// 두개가 나와야 한다
		Assertions.assertEquals(2, result.queryResults().size()); // 페이지당 2개
		Assertions.assertEquals(1, result.currentPageNumber()); // 페이지 번호
		Assertions.assertEquals(2, result.currentPageSize()); // 페이지 크기
		Assertions.assertEquals(1, result.possibleNextPageNumbers().size()); // 다음 페이지가 1개 존재한다

		// 다음 페이지도 테스트
		final SingleProblemReadModelPageResult nextResult = queryService.queryPage(query, null, null, 1000L, 2, 2);

		// 다음 페이지에 1개가존재해야한다
		Assertions.assertEquals(1, nextResult.queryResults().size());

		// 다음 페이지는 없다
		Assertions.assertTrue(nextResult.possibleNextPageNumbers().isEmpty());
	}

	@AfterEach
	void clear() {
		singleProblemReadModelRepository.deleteAll();
	}
}
