package kr.co.mathrank.domain.board.viewcount.entity;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kr.co.mathrank.domain.board.viewcount.repository.ViewCountBackupRepository;

@SpringBootTest
@Transactional
class ViewCountTest {
	@Autowired
	private ViewCountBackupRepository viewCountBackupRepository;
	@PersistenceContext
	private EntityManager entityManager;

	@Test
	void 삽입될떄_업데이트_시간_업데이트_된다() {
		final ViewCount viewCount = ViewCount.of(1L, 10L);
		viewCountBackupRepository.save(viewCount);

		entityManager.flush();
		entityManager.clear();

		final ViewCount actualViewCount = viewCountBackupRepository.findAll().getFirst();
		Assertions.assertNotNull(actualViewCount);
	}

	@Test
	void 쓰기후_업데이트시간이_변경된다() {
		final ViewCount viewCount = ViewCount.of(1L, 10L);
		final Long updatedViewCount = 20L;
		viewCountBackupRepository.save(viewCount);

		entityManager.flush();
		entityManager.clear();

		final ViewCount actualViewCount = viewCountBackupRepository.findAll().getFirst();
		actualViewCount.setViewCount(updatedViewCount);
		final LocalDateTime before = actualViewCount.getUpdatedAt();

		entityManager.flush();
		entityManager.clear();

		final ViewCount updatedViewCountEntity = viewCountBackupRepository.findAll().getFirst();

		Assertions.assertTrue(before.isBefore(updatedViewCountEntity.getUpdatedAt()));
	}
}