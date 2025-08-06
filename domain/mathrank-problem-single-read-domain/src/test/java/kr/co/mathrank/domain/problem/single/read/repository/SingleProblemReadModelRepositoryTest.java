package kr.co.mathrank.domain.problem.single.read.repository;

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

import kr.co.mathrank.domain.problem.core.AnswerType;
import kr.co.mathrank.domain.problem.core.Difficulty;
import kr.co.mathrank.domain.problem.single.read.dto.SingleProblemReadModelQuery;
import kr.co.mathrank.domain.problem.single.read.entity.SingleProblemReadModel;

@Transactional
@SpringBootTest(
	properties =
		"""
			spring.jpa.show-sql=true
			spring.jpa.hibernate.ddl-auto=create
			"""
)
@Testcontainers
class SingleProblemReadModelRepositoryTest {
	@Autowired
	private SingleProblemReadModelRepository singleProblemReadModelRepository;

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
	void 난이도를_조건으로_검색한다() {
		singleProblemReadModelRepository.saveAll(List.of(
			SingleProblemReadModel.of(
				1L,
				1L,
				"testImage",
				"path",
				AnswerType.MULTIPLE_CHOICE,
				Difficulty.KILLER, // 가중치 60
				100L,
				2000L,
				1000L
			),
			SingleProblemReadModel.of(
				2L,
				2L,
				"testImage",
				"path",
				AnswerType.MULTIPLE_CHOICE,
				Difficulty.MID, // 가중치 30
				100L,
				2000L,
				1000L
			),
			SingleProblemReadModel.of(
				3L,
				3L,
				"testImage",
				"path",
				AnswerType.MULTIPLE_CHOICE,
				Difficulty.LOW, // 가중치 10
				100L,
				2000L,
				1000L
			)
		));

		Assertions.assertAll(
			// MID 이상의 결과는 두개여야 한다.
			() -> Assertions.assertEquals(2, singleProblemReadModelRepository.queryPage(
				new SingleProblemReadModelQuery(
					null,
					null,
					null,
					Difficulty.MID,
					Difficulty.KILLER,
					null,
					null,
					null,
					null
				),
				10,
				1
			).size()),
			// 한쪽 조건이 null 일시, 모두 포함한다
			() -> Assertions.assertEquals(2, singleProblemReadModelRepository.queryPage(
				new SingleProblemReadModelQuery(
					null,
					null,
					null,
					Difficulty.MID,
					null,
					null,
					null,
					null,
					null
				),
				10,
				1
			).size()),
			// max, min 이 같을땐 하나가 리턴되야 한다
			() -> Assertions.assertEquals(1, singleProblemReadModelRepository.queryPage(
				new SingleProblemReadModelQuery(
					null,
					null,
					null,
					Difficulty.MID,
					Difficulty.MID,
					null,
					null,
					null,
					null
				),
				10,
				1
			).size())
		);
	}

	@Test
	void 정답률을_조건으로_검색한다() {
		singleProblemReadModelRepository.saveAll(List.of(

			// 정답률 10
			SingleProblemReadModel.of(
				1L,
				1L,
				"testImage",
				"path",
				null,
				null,
				100L,
				2000L,
				1000L
			),

			// 정답률 20
			SingleProblemReadModel.of(
				2L,
				2L,
				"testImage",
				"path",
				null,
				null,
				200L,
				2000L,
				1000L
			),

			// 정답률 30
			SingleProblemReadModel.of(
				3L,
				3L,
				"testImage",
				"path",
				null,
				null,
				300L,
				2000L,
				1000L
			)
		));

		Assertions.assertAll(
			// 10 ~ 30 사이는 3개
			() -> Assertions.assertEquals(3, singleProblemReadModelRepository.queryPage(
				new SingleProblemReadModelQuery(
					null,
					null,
					null,
					null,
					null,
					10,
					30,
					null,
					null
				),
				10,
				1
			).size()),
			// 10 ~ 20은 두개
			() -> Assertions.assertEquals(2, singleProblemReadModelRepository.queryPage(
				new SingleProblemReadModelQuery(
					null,
					null,
					null,
					null,
					null,
					10,
					20,
					null,
					null
				),
				10,
				1
			).size())
		);
	}

	@Test
	void 코스경로를_조건으로_검색한다() {
		singleProblemReadModelRepository.saveAll(List.of(
			SingleProblemReadModel.of(1L, 1L, "image", "math/algebra", null, null, 100L, 200L, 100L),
			SingleProblemReadModel.of(2L, 2L, "image", "math/geometry", null, null, 100L, 200L, 100L),
			SingleProblemReadModel.of(3L, 3L, "image", "science/physics", null, null, 100L, 200L, 100L)
		));

		Assertions.assertAll(
			// math로 시작하는 것만
			() -> Assertions.assertEquals(2, singleProblemReadModelRepository.queryPage(
				new SingleProblemReadModelQuery(
					null, "math", null, null, null,
					null, null, null, null
				),
				10, 1
			).size()),

			// science로 시작하는 것만
			() -> Assertions.assertEquals(1, singleProblemReadModelRepository.queryPage(
				new SingleProblemReadModelQuery(
					null, "science", null, null, null,
					null, null, null, null
				),
				10, 1
			).size()),

			// 존재하지 않는 경로
			() -> Assertions.assertEquals(0, singleProblemReadModelRepository.queryPage(
				new SingleProblemReadModelQuery(
					null, "english", null, null, null,
					null, null, null, null
				),
				10, 1
			).size())
		);
	}

	@Test
	void 시도수를_조건으로_검색한다() {
		singleProblemReadModelRepository.saveAll(List.of(
			SingleProblemReadModel.of(1L, 1L, "image", "path", null, null, 100L, 200L, 100L),
			// totalAttemptedCount = 200
			SingleProblemReadModel.of(2L, 2L, "image", "path", null, null, 100L, 300L, 100L),
			// totalAttemptedCount = 300
			SingleProblemReadModel.of(3L, 3L, "image", "path", null, null, 100L, 500L, 100L)
			// totalAttemptedCount = 500
		));

		Assertions.assertAll(
			// 200~300이면 두 개
			() -> Assertions.assertEquals(2, singleProblemReadModelRepository.queryPage(
				new SingleProblemReadModelQuery(
					null, null, null, null, null,
					null, null,
					200L, 300L
				),
				10, 1
			).size()),

			// 최대값만 지정 시 500 이하 전부
			() -> Assertions.assertEquals(3, singleProblemReadModelRepository.queryPage(
				new SingleProblemReadModelQuery(
					null, null, null, null, null,
					null, null,
					null, 500L
				),
				10, 1
			).size()),

			// 400 이상은 하나만
			() -> Assertions.assertEquals(1, singleProblemReadModelRepository.queryPage(
				new SingleProblemReadModelQuery(
					null, null, null, null, null,
					null, null,
					400L, null
				),
				10, 1
			).size())
		);
	}

	@Test
	void 복합조건으로_검색한다() {
		singleProblemReadModelRepository.saveAll(List.of(
			// 해당 조건에 부합 (정답률 30, 시도수 500, 난이도 MID, 코스 "math")
			SingleProblemReadModel.of(1L, 1L, "image", "math/algebra", null, Difficulty.MID, 300L, 500L, 1000L),

			// 정답률 미달 (20)
			SingleProblemReadModel.of(2L, 2L, "image", "math/algebra", null, Difficulty.MID, 200L, 500L, 1000L),

			// 시도수 초과 (600)
			SingleProblemReadModel.of(3L, 3L, "image", "math/algebra", null, Difficulty.MID, 300L, 600L, 1000L),

			// 난이도 다름 (LOW)
			SingleProblemReadModel.of(4L, 4L, "image", "math/algebra", null, Difficulty.LOW, 300L, 500L, 1000L),

			// 코스경로 다름 ("science")
			SingleProblemReadModel.of(5L, 5L, "image", "science/physics", null, Difficulty.MID, 300L, 500L, 1000L)
		));

		// 조건: 정답률 [30~30], 시도수 [500~500], 난이도 MID, 코스경로 "math"
		final SingleProblemReadModelQuery query = new SingleProblemReadModelQuery(
			null,
			"math",
			null,
			Difficulty.MID,
			Difficulty.MID,
			30,
			30,
			500L,
			500L
		);

		Assertions.assertEquals(1, singleProblemReadModelRepository.queryPage(query, 10, 1).size());
	}

	@Test
	void 정답유형을_조건으로_검색한다() {
		singleProblemReadModelRepository.saveAll(List.of(
			SingleProblemReadModel.of(1L, 1L, "image", "path", AnswerType.MULTIPLE_CHOICE, null, 100L, 200L, 100L),
			SingleProblemReadModel.of(2L, 2L, "image", "path", AnswerType.MULTIPLE_CHOICE, null, 100L, 200L, 100L),
			SingleProblemReadModel.of(3L, 3L, "image", "path", AnswerType.SHORT_ANSWER, null, 100L, 200L, 100L)
		));

		Assertions.assertAll(
			// MULTIPLE_CHOICE만 검색하면 2개
			() -> Assertions.assertEquals(2, singleProblemReadModelRepository.queryPage(
				new SingleProblemReadModelQuery(
					null, null, AnswerType.MULTIPLE_CHOICE, null, null,
					null, null, null, null
				), 10, 1
			).size()),

			// SHORT_ANSWER만 검색하면 1개
			() -> Assertions.assertEquals(1, singleProblemReadModelRepository.queryPage(
				new SingleProblemReadModelQuery(
					null, null, AnswerType.SHORT_ANSWER, null, null,
					null, null, null, null
				),
				10, 1
			).size()),

			// null로하면 모두 조회 ( 3개 )
			() -> Assertions.assertEquals(3, singleProblemReadModelRepository.queryPage(
				new SingleProblemReadModelQuery(
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					null
					),
				10, 1
			).size())
		);
	}

}
