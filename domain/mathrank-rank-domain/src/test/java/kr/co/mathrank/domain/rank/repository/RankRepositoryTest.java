package kr.co.mathrank.domain.rank.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class RankRepositoryTest {
	@Autowired
	private RankRepository rankRepository;

	private static GenericContainer<?> redisContainer = new GenericContainer<>("redis:6.0.2")
		.withExposedPorts(6379);

	@DynamicPropertySource
	static void init(DynamicPropertyRegistry registry) {
		redisContainer.start();
		registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379));
		registry.add("spring.data.redis.host", () -> redisContainer.getHost());
	}

	@Test
	void 서로_다른_점수일때_내림차순_랭크() {
		for (int i = 0; i < 100; i++) {
			rankRepository.set(String.valueOf(i), (long)i);
		}

		Assertions.assertAll(
			// 0 번 멤버는 100명 중 꼴등
			() -> Assertions.assertEquals(100, rankRepository.getRank(String.valueOf(0))),
			// 99번 멤버는 100명중 1등
			() -> Assertions.assertEquals(1, rankRepository.getRank(String.valueOf(99)))
		);
	}

	@Test
	void 동점자는_같은_등수() {
		for (int i = 0; i < 10; i++) {
			// 1점 10명
			rankRepository.set(String.valueOf(i), 1L);
		}

		for (int i = 10; i < 20; i++) {
			// 2점 10명
			rankRepository.set(String.valueOf(i), 2L);
		}

		for (int i = 20; i < 30; i++) {
			// 3점 10명
			rankRepository.set(String.valueOf(i), 3L);
		}

		Assertions.assertAll(
			// 0번 사용자는 21등
			() -> Assertions.assertEquals(21, rankRepository.getRank(String.valueOf(0))),

			// 10번 사용자는 11 등
			() -> Assertions.assertEquals(11, rankRepository.getRank(String.valueOf(10))),

			// 20 번 사용자는 1등
			() -> Assertions.assertEquals(1, rankRepository.getRank(String.valueOf(20)))
		);
	}

	@Test
	void 사용자수_조회_모두_조회() {
		final int totalCount = 10;
		for (int i = 0; i < totalCount; i++) {
			// 1점 10명
			rankRepository.set(String.valueOf(i), 1L);
		}

		Assertions.assertEquals(totalCount, rankRepository.getTotalRankerCount());
	}

	@AfterEach
	void clear() {
		rankRepository.clear();
	}
}