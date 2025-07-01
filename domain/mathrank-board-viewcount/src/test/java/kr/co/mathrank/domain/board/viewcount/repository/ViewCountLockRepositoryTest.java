package kr.co.mathrank.domain.board.viewcount.repository;

import java.time.Duration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;

@SpringBootTest
class ViewCountLockRepositoryTest {
	@Autowired
	private ViewCountLockRepository viewCountLockRepository;

	static GenericContainer<?> genericContainer = new GenericContainer<>("redis:7.4")
		.withExposedPorts(6379);

	@DynamicPropertySource
	static void redisProperties(DynamicPropertyRegistry registry) {
		genericContainer.start();
		registry.add("spring.data.redis.host", genericContainer::getHost);
		registry.add("spring.data.redis.port", () -> genericContainer.getMappedPort(6379));
	}

	@Test
	void 일정시간_락이_걸린다() throws InterruptedException {
		final Long boardId = 1L;
		final Long requestMemberId = 1L;
		final Duration duration = Duration.ofSeconds(1);

		Assertions.assertTrue(viewCountLockRepository.lock(boardId, requestMemberId, duration));
		Assertions.assertFalse(viewCountLockRepository.lock(boardId, requestMemberId, duration));

		Thread.sleep(1100); // 1.1초 대기

		Assertions.assertTrue(viewCountLockRepository.lock(boardId, requestMemberId, duration));
	}
}