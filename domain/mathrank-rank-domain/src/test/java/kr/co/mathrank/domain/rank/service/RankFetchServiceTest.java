package kr.co.mathrank.domain.rank.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import kr.co.mathrank.domain.rank.dto.SolveLogRegisterCommand;
import kr.co.mathrank.domain.rank.repository.RankRepository;

@SpringBootTest
@Testcontainers
@Transactional
class RankFetchServiceTest {
	@Autowired
	private SolveLogSaveManager solveLogSaveManager;
	@Autowired
	private RankRepository rankRepository;
	@Autowired
	private RankFetchService rankRefreshService;

	@Container
	private static GenericContainer<?> redisContainer = new GenericContainer<>("redis:6.2.2")
		.withExposedPorts(6379);

	@Container
	static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0.42")
		.withDatabaseName("testdb")
		.withUsername("user")
		.withPassword("password");

	@DynamicPropertySource
	static void init(DynamicPropertyRegistry registry) {
		registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379));
		registry.add("spring.data.redis.host", () -> redisContainer.getHost());
	}

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", mysql::getJdbcUrl);
		registry.add("spring.datasource.username", mysql::getUsername);
		registry.add("spring.datasource.password", mysql::getPassword);
	}

	@Test
	void 레디스에_저장된_값보다_클때_디비값이_레디스에_반영된다() {
		final Long userId = 1L;

		// 사용자 1번 1점
		rankRepository.set(String.valueOf(userId), 1L);

		// 사용자 1번 10점
		solveLogSaveManager.save(
			new SolveLogRegisterCommand(
				1L,
				2L,
				userId,
				true),
			10);

		rankRefreshService.syncRedis();

		Assertions.assertEquals(10, rankRepository.getScore(String.valueOf(userId)));
	}

	@Test
	void 레디스에_저장된_값보다_작으면_무시한다() {
		final Long userId = 1L;

		// 사용자 1번 100점
		rankRepository.set(String.valueOf(userId), 100L);

		// 사용자 1번 10점
		solveLogSaveManager.save(
			new SolveLogRegisterCommand(
				1L,
				2L,
				userId,
				true),
			10);

		rankRefreshService.syncRedis();

		// 기존 100점 그대로
		Assertions.assertEquals(100, rankRepository.getScore(String.valueOf(userId)));
	}

	@Test
	void 레디스에_없으면_저장한다() {
		final Long userId = 1L;

		// 사용자 1번 10점
		solveLogSaveManager.save(
			new SolveLogRegisterCommand(
				1L,
				2L,
				userId,
				true),
			10);

		rankRefreshService.syncRedis();

		// 기존 100점 그대로
		Assertions.assertEquals(10, rankRepository.getScore(String.valueOf(userId)));
	}

	@AfterEach
	void clear() {
		rankRepository.clear();
	}
}
