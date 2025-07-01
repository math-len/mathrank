package kr.co.mathrank.domain.board.viewcount.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;

@SpringBootTest
class ViewCountRepositoryTest {
	@Autowired
	private ViewCountRepository viewCountRepository;

	@Container
	static GenericContainer<?> redisContainer = new GenericContainer<>("redis:7.4")
		.withExposedPorts(6379);

	@DynamicPropertySource
	static void redisProperties(DynamicPropertyRegistry registry) {
		redisContainer.start();
		registry.add("spring.data.redis.host", redisContainer::getHost);
		registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379));
	}

	@Test
	void 조회수_증가() {
		final Long boardId = 1L;

		Long viewCount = viewCountRepository.increase(boardId);
		assertEquals(1L, viewCount);

		viewCount = viewCountRepository.get(boardId);
		assertEquals(1L, viewCount);

		viewCount = viewCountRepository.increase(boardId);
		assertEquals(2L, viewCount);

		viewCount = viewCountRepository.get(boardId);
		assertEquals(2L, viewCount);
	}
}
