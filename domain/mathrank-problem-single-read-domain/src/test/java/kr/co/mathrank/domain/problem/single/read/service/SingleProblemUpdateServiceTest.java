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
import kr.co.mathrank.domain.problem.single.read.dto.SingleProblemAttemptStatsUpdateCommand;
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

	@Test
	void updatedAt이_더_최신일_경우_통계가_정상적으로_업데이트된다() {
		final Long singleProblemId = 1L;
		final LocalDateTime beforeTime = LocalDateTime.now();
		final LocalDateTime updatedTime = beforeTime.plusMinutes(1);

		// 기존 모델 저장
		final SingleProblemReadModel model = SingleProblemReadModel.of(
			1L,
			singleProblemId,
			"img",
			"path",
			AnswerType.MULTIPLE_CHOICE,
			Difficulty.MID,
			100L,
			2000L,
			1000L,
			beforeTime
		);
		singleProblemReadModelRepository.save(model);
		entityManager.flush();
		entityManager.clear();

		// When: 더 최신 updatedAt으로 커맨드 실행
		final SingleProblemAttemptStatsUpdateCommand command = new SingleProblemAttemptStatsUpdateCommand(
			singleProblemId,
			200L, 300L, 4000L,
			updatedTime
		);

		singleProblemUpdateService.updateAttemptStatistics(command);
		entityManager.flush();
		entityManager.clear();

		// Then: 업데이트가 반영되어야 한다.
		final SingleProblemReadModel updated = singleProblemReadModelRepository.findById(singleProblemId).orElseThrow();
		Assertions.assertAll(
			() -> Assertions.assertEquals(200L, updated.getFirstTrySuccessCount()),
			() -> Assertions.assertEquals(300L, updated.getTotalAttemptedCount()),
			() -> Assertions.assertEquals(4000L, updated.getAttemptedUserDistinctCount()),
			() -> Assertions.assertEquals(updatedTime, updated.getUpdatedAt())
		);
	}

	@Test
	void updatedAt이_과거인_경우_통계가_업데이트되지_않는다() {
		// Given
		final Long singleProblemId = 2L;
		final LocalDateTime initialTime = LocalDateTime.now();
		final LocalDateTime pastTime = initialTime.minusHours(1);

		final SingleProblemReadModel model = SingleProblemReadModel.of(
			2L,
			singleProblemId,
			"img",
			"path",
			AnswerType.MULTIPLE_CHOICE,
			Difficulty.MID,
			100L,
			2000L,
			1000L,
			initialTime
		);
		singleProblemReadModelRepository.save(model);
		entityManager.flush();
		entityManager.clear();

		// When: 더 과거 updatedAt으로 커맨드 실행
		final SingleProblemAttemptStatsUpdateCommand command = new SingleProblemAttemptStatsUpdateCommand(
			singleProblemId,
			999L, 999L, 9999L,
			pastTime
		);

		singleProblemUpdateService.updateAttemptStatistics(command);
		entityManager.flush();
		entityManager.clear();

		// Then: 기존 값이 유지되어야 한다.
		final SingleProblemReadModel updated = singleProblemReadModelRepository.findById(singleProblemId).orElseThrow();
		Assertions.assertAll(
			() -> Assertions.assertEquals(100L, updated.getFirstTrySuccessCount()),
			() -> Assertions.assertEquals(2000L, updated.getTotalAttemptedCount()),
			() -> Assertions.assertEquals(1000L, updated.getAttemptedUserDistinctCount()),
			() -> Assertions.assertEquals(initialTime, updated.getUpdatedAt())
		);
	}
}
