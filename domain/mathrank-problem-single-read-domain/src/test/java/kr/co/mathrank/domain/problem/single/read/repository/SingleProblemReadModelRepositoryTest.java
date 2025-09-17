package kr.co.mathrank.domain.problem.single.read.repository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import kr.co.mathrank.client.internal.course.CourseClient;
import kr.co.mathrank.domain.problem.core.AnswerType;
import kr.co.mathrank.domain.problem.core.Difficulty;
import kr.co.mathrank.domain.problem.single.read.dto.SingleProblemReadModelQuery;
import kr.co.mathrank.domain.problem.single.read.entity.OrderColumn;
import kr.co.mathrank.domain.problem.single.read.entity.OrderDirection;
import kr.co.mathrank.domain.problem.single.read.entity.SingleProblemReadModel;

@Transactional
@SpringBootTest
@Testcontainers
class SingleProblemReadModelRepositoryTest {
	@MockitoBean
	private CourseClient courseClient;
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
	void 최신순으로_조회한다() {
		final LocalDateTime dateTime = LocalDateTime.of(2000, 11, 21, 10, 20);
		// 1, 2, 3 에 각각 100시간씩 더함

		singleProblemReadModelRepository.saveAll(List.of(
			SingleProblemReadModel.of(
				1L,
				1L,
				"singleProblemName",
				"testImage",
				"path",
				AnswerType.MULTIPLE_CHOICE,
				Difficulty.MID, // 가중치 30
				100L,
				2000L,
				1000L,
				dateTime
			),
			SingleProblemReadModel.of(
				2L,
				2L,
				"singleProblemName",
				"testImage",
				"path",
				AnswerType.MULTIPLE_CHOICE,
				Difficulty.MID, // 가중치 30
				100L,
				2000L,
				1000L,
				dateTime.plus(100, ChronoUnit.HOURS)
			),
			SingleProblemReadModel.of(
				3L,
				3L,
				"singleProblemName",
				"testImage",
				"path",
				AnswerType.MULTIPLE_CHOICE,
				Difficulty.MID, // 가중치 30
				100L,
				2000L,
				1000L,
				dateTime.plus(200, ChronoUnit.HOURS)
			)
		));
		// 내림차순 조회의 결과는 3, 2, 1로 나와야 함
		final List<SingleProblemReadModel> models = singleProblemReadModelRepository.queryPage(
			new SingleProblemReadModelQuery(
				null,
				"singleProblemName",
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
			),
			10,
			1,
			OrderColumn.DATE,
			OrderDirection.DESC
		);

		Assertions.assertAll(
			() -> Assertions.assertEquals(3, models.get(0).getId()),
			() -> Assertions.assertEquals(2, models.get(1).getId()),
			() -> Assertions.assertEquals(1, models.get(2).getId())
		);
	}

	@Test
	void 풀이횟수_내림차순_으로_조회한다() {
		final LocalDateTime dateTime = LocalDateTime.of(2000, 11, 21, 10, 20);

		singleProblemReadModelRepository.saveAll(List.of(
			SingleProblemReadModel.of(
				1L,
				1L,
				"singleProblemName",
				"testImage",
				"path",
				AnswerType.MULTIPLE_CHOICE,
				Difficulty.MID,
				100L,
				1L, // 정렬기준
				1000L,
				dateTime
			),
			SingleProblemReadModel.of(
				2L,
				2L,
				"singleProblemName",
				"testImage",
				"path",
				AnswerType.MULTIPLE_CHOICE,
				Difficulty.MID,
				100L,
				2L, // 정력 기준
				1000L,
				dateTime
			),
			SingleProblemReadModel.of(
				3L,
				3L,
				"singleProblemName",
				"testImage",
				"path",
				AnswerType.MULTIPLE_CHOICE,
				Difficulty.MID,
				100L,
				3L, // 정렬기준
				1000L,
				dateTime
			)
		));

		// 내림차순 조회의 결과는 3, 2, 1로 나와야 함
		final List<SingleProblemReadModel> models = singleProblemReadModelRepository.queryPage(
			new SingleProblemReadModelQuery(
				null,
				"singleProblemName",
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
			),
			10,
			1,
			OrderColumn.TOTAL_TRY_COUNT,
			OrderDirection.DESC
		);

		Assertions.assertAll(
			() -> Assertions.assertEquals(3, models.get(0).getId()),
			() -> Assertions.assertEquals(2, models.get(1).getId()),
			() -> Assertions.assertEquals(1, models.get(2).getId())
		);
	}

	@Test
	void 난이도를_조건으로_검색한다() {
		singleProblemReadModelRepository.saveAll(List.of(
			SingleProblemReadModel.of(
				1L,
				1L,
				"singleProblemName",
				"testImage",
				"path",
				AnswerType.MULTIPLE_CHOICE,
				Difficulty.KILLER, // 가중치 60
				100L,
				2000L,
				1000L,
				LocalDateTime.now()
			),
			SingleProblemReadModel.of(
				2L,
				2L,
				"singleProblemName",
				"testImage",
				"path",
				AnswerType.MULTIPLE_CHOICE,
				Difficulty.MID, // 가중치 30
				100L,
				2000L,
				1000L,
				LocalDateTime.now()
			),
			SingleProblemReadModel.of(
				3L,
				3L,
				"singleProblemName",
				"testImage",
				"path",
				AnswerType.MULTIPLE_CHOICE,
				Difficulty.LOW, // 가중치 10
				100L,
				2000L,
				1000L,
				LocalDateTime.now()
			)
		));

		Assertions.assertAll(
			// MID 이상의 결과는 두개여야 한다.
			() -> Assertions.assertEquals(2, singleProblemReadModelRepository.queryPage(
				new SingleProblemReadModelQuery(
					null,
					"singleProblemName",
					null,
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
				1,
				null,
				null
			).size()),
			// 한쪽 조건이 null 일시, 모두 포함한다
			() -> Assertions.assertEquals(2, singleProblemReadModelRepository.queryPage(
				new SingleProblemReadModelQuery(
					null,
					"singleProblemName",
					null,
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
				1,
				null,
				null
			).size()),
			// max, min 이 같을땐 하나가 리턴되야 한다
			() -> Assertions.assertEquals(1, singleProblemReadModelRepository.queryPage(
				new SingleProblemReadModelQuery(
					null,
					"singleProblemName",
					null,
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
				,
				null,
				null
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
				"singleProblemName",
				"testImage",
				"path",
				null,
				null,
				100L,
				2000L,
				1000L,
				LocalDateTime.now()
			),

			// 정답률 20
			SingleProblemReadModel.of(
				2L,
				2L,
				"singleProblemName",
				"testImage",
				"path",
				null,
				null,
				200L,
				2000L,
				1000L,
				LocalDateTime.now()
			),

			// 정답률 30
			SingleProblemReadModel.of(
				3L,
				3L,
				"singleProblemName",
				"testImage",
				"path",
				null,
				null,
				300L,
				2000L,
				1000L,
				LocalDateTime.now()
			)
		));

		Assertions.assertAll(
			// 10 ~ 30 사이는 3개
			() -> Assertions.assertEquals(3, singleProblemReadModelRepository.queryPage(
				new SingleProblemReadModelQuery(
					null,
					"singleProblemName",
					null,
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
				1,
				null,
				null
			).size()),
			// 10 ~ 20은 두개
			() -> Assertions.assertEquals(2, singleProblemReadModelRepository.queryPage(
				new SingleProblemReadModelQuery(
					null,
					"singleProblemName",
					null,
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
				1,
				null,
				null
			).size())
		);
	}

	@Test
	void 코스경로를_조건으로_검색한다() {
		singleProblemReadModelRepository.saveAll(List.of(
			SingleProblemReadModel.of(1L, 1L, "singleProblemName","image", "math/algebra", null, null, 100L, 200L, 100L,
				LocalDateTime.now()),
			SingleProblemReadModel.of(2L, 2L, "singleProblemName","image", "math/geometry", null, null, 100L, 200L, 100L,
				LocalDateTime.now()),
			SingleProblemReadModel.of(3L, 3L, "singleProblemName","image", "science/physics", null, null, 100L, 200L, 100L,
				LocalDateTime.now())
		));

		Assertions.assertAll(
			// math로 시작하는 것만
			() -> Assertions.assertEquals(2, singleProblemReadModelRepository.queryPage(
				new SingleProblemReadModelQuery(
					null, "singleProblemName","math", null,
					null,null, null, null,
					null, null, null, null
				),
				10, 1, null, null
			).size()),

			// science로 시작하는 것만
			() -> Assertions.assertEquals(1, singleProblemReadModelRepository.queryPage(
				new SingleProblemReadModelQuery(
					null, "singleProblemName", "science", null, null, null,
					null,null,
					null, null, null, null
				),
				10, 1,
				null,
				null
			).size()),

			// 존재하지 않는 경로
			() -> Assertions.assertEquals(0, singleProblemReadModelRepository.queryPage(
				new SingleProblemReadModelQuery(
					null, "singleProblemName","english", null, null, null,
					null,null,
					null, null, null, null
				),
				10, 1,
				null,
				null
			).size())
		);
	}

	@Test
	void 시도수를_조건으로_검색한다() {
		singleProblemReadModelRepository.saveAll(List.of(
			SingleProblemReadModel.of(1L, 1L, "singleProblemName","image", "path", null, null, 100L, 200L, 100L, LocalDateTime.now()),
			// totalAttemptedCount = 200
			SingleProblemReadModel.of(2L, 2L, "singleProblemName","image", "path", null, null, 100L, 300L, 100L, LocalDateTime.now()),
			// totalAttemptedCount = 300
			SingleProblemReadModel.of(3L, 3L, "singleProblemName","image", "path", null, null, 100L, 500L, 100L, LocalDateTime.now())
			// totalAttemptedCount = 500
		));

		Assertions.assertAll(
			// 200~300이면 두 개
			() -> Assertions.assertEquals(2, singleProblemReadModelRepository.queryPage(
				new SingleProblemReadModelQuery(
					null, "singleProblemName",null, null, null, null,
					null, null,null,
					null,
					200L, 300L
				),
				10, 1, null, null
			).size()),

			// 최대값만 지정 시 500 이하 전부
			() -> Assertions.assertEquals(3, singleProblemReadModelRepository.queryPage(
				new SingleProblemReadModelQuery(
					null, "singleProblemName",null, null, null, null,null,
					null,
					null, null,
					null, 500L
				),
				10, 1,
				null,
				null
			).size()),

			// 400 이상은 하나만
			() -> Assertions.assertEquals(1, singleProblemReadModelRepository.queryPage(
				new SingleProblemReadModelQuery(
					null, "singleProblemName",null, null, null, null,null,
					null,
					null, null,
					400L, null
				),
				10, 1,
				null,
				null
			).size())
		);
	}

	@Test
	void 복합조건으로_검색한다() {
		singleProblemReadModelRepository.saveAll(List.of(
			// 해당 조건에 부합 (정답률 30, 시도수 500, 난이도 MID, 코스 "math")
			SingleProblemReadModel.of(1L, 1L, "singleProblemName","image", "math/algebra", null, Difficulty.MID, 300L, 500L, 1000L, LocalDateTime.now()),

			// 정답률 미달 (20)
			SingleProblemReadModel.of(2L, 2L, "singleProblemName","image", "math/algebra", null, Difficulty.MID, 200L, 500L, 1000L, LocalDateTime.now()),

			// 시도수 초과 (600)
			SingleProblemReadModel.of(3L, 3L, "singleProblemName","image", "math/algebra", null, Difficulty.MID, 300L, 600L, 1000L, LocalDateTime.now()),

			// 난이도 다름 (LOW)
			SingleProblemReadModel.of(4L, 4L, "singleProblemName","image", "math/algebra", null, Difficulty.LOW, 300L, 500L, 1000L, LocalDateTime.now()),

			// 코스경로 다름 ("science")
			SingleProblemReadModel.of(5L, 5L, "singleProblemName","image", "science/physics", null, Difficulty.MID, 300L, 500L, 1000L, LocalDateTime.now())
		));

		// 조건: 정답률 [30~30], 시도수 [500~500], 난이도 MID, 코스경로 "math"
		final SingleProblemReadModelQuery query = new SingleProblemReadModelQuery(
			null,
			"singleProblemName",
			"math",
			null,
			null,
			null,
			Difficulty.MID,
			Difficulty.MID,
			30,
			30,
			500L,
			500L
		);

		Assertions.assertEquals(1, singleProblemReadModelRepository.queryPage(query, 10, 1, null, null).size());
	}

	@Test
	void 정답유형을_조건으로_검색한다() {
		singleProblemReadModelRepository.saveAll(List.of(
			SingleProblemReadModel.of(1L, 1L, "singleProblemName","image", "path", AnswerType.MULTIPLE_CHOICE, null, 100L, 200L, 100L, LocalDateTime.now()),
			SingleProblemReadModel.of(2L, 2L, "singleProblemName","image", "path", AnswerType.MULTIPLE_CHOICE, null, 100L, 200L, 100L, LocalDateTime.now()),
			SingleProblemReadModel.of(3L, 3L, "singleProblemName","image", "path", AnswerType.SHORT_ANSWER, null, 100L, 200L, 100L, LocalDateTime.now())
		));

		Assertions.assertAll(
			// MULTIPLE_CHOICE만 검색하면 2개
			() -> Assertions.assertEquals(2, singleProblemReadModelRepository.queryPage(
				new SingleProblemReadModelQuery(
					null, "singleProblemName",null, null,
					null, AnswerType.MULTIPLE_CHOICE, null, null,
					null, null, null, null
				), 10, 1, null, null
			).size()),

			// SHORT_ANSWER만 검색하면 1개
			() -> Assertions.assertEquals(1, singleProblemReadModelRepository.queryPage(
				new SingleProblemReadModelQuery(
					null, "singleProblemName",null,null,
					null, AnswerType.SHORT_ANSWER, null, null,
					null, null, null, null
				),
				10, 1,
				null,
				null
			).size()),

			// null로하면 모두 조회 ( 3개 )
			() -> Assertions.assertEquals(3, singleProblemReadModelRepository.queryPage(
				new SingleProblemReadModelQuery(
					null,
					"singleProblemName",
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
					),
				10, 1,
				null,
				null
			).size())
		);
	}

	@Test
	void 문제이름을_조건으로_검색한다() {
		// given
		singleProblemReadModelRepository.saveAll(List.of(
			SingleProblemReadModel.of(1L, 1L, "problem-A", "image", "path", null, null, 100L, 200L, 100L, LocalDateTime.now()),
			SingleProblemReadModel.of(2L, 2L, "problem-B", "image", "path", null, null, 100L, 200L, 100L, LocalDateTime.now()),
			SingleProblemReadModel.of(3L, 3L, "another-problem", "image", "path", null, null, 100L, 200L, 100L, LocalDateTime.now()),
			SingleProblemReadModel.of(4L, 4L, "no", "image", "path", null, null, 100L, 200L, 100L, LocalDateTime.now())
		));

		// when
		final SingleProblemReadModelQuery query = new SingleProblemReadModelQuery(
			null,"problem", null, null,
			null,null, null, null, null, null, null, null
		);
		final List<SingleProblemReadModel> result = singleProblemReadModelRepository.queryPage(query, 10, 1, null, null);

		// then
		Assertions.assertEquals(3, result.size());
	}
}
