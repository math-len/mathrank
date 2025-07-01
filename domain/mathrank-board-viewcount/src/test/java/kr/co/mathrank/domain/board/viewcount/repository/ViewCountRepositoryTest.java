package kr.co.mathrank.domain.board.viewcount.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;

import kr.co.mathrank.domain.board.viewcount.entity.ViewCount;

@SpringBootTest
class ViewCountRepositoryTest {
	@Autowired
	private ViewCountRepository viewCountRepository;
	@Autowired
	private ViewCountBackupRepository backUpRepository;

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

	@Test
	void 레디스가_비어있을때_조회수_증회_시_백업레포에서_조회수_가져오기() {
		final Long boardId = 2L;
		final Long backupViewCount = 100L;
		backUpRepository.save(ViewCount.of(boardId, backupViewCount));

		// 백업 레포에 값이 없으므로, 초기값은 0이어야 한다.
		Long viewCount = viewCountRepository.get(boardId);
		assertEquals(backupViewCount, viewCount);
	}

	@Test
	void 레디스가_비어있을때_조회수_증가시_백업레포에서_조회수_가져오기() {
		final Long boardId = 3L;
		final Long backupViewCount = 100L;
		backUpRepository.save(ViewCount.of(boardId, backupViewCount));

		// 레디스에 값이 없으므로, 백업 레포에서 조회수를 가져와야 한다.
		Long viewCount = viewCountRepository.increase(boardId);
		assertEquals(backupViewCount + 1, viewCount);
	}
}
