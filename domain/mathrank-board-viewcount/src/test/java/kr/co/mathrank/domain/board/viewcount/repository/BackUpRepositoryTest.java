package kr.co.mathrank.domain.board.viewcount.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kr.co.mathrank.domain.board.viewcount.entity.ViewCount;

@SpringBootTest
@Transactional
class BackUpRepositoryTest {
	@Autowired
	private ViewCountBackupRepository backUpRepository;
	@PersistenceContext
	private EntityManager entityManager;

	@Test
	void 없는놈_조회시_NULL이_반환된다() {
		final Long postId = 1L;
		assertNull(backUpRepository.getViewCountByPostId(postId));
	}

	@Test
	void 있는놈_조회시_값이_반환된다() {
		final Long postId = 1L;
		final Long expectedViewCount = 10L;

		// Given: 백업 레포에 값이 존재한다.
		final ViewCount viewCount = ViewCount.of(postId, expectedViewCount);
		backUpRepository.save(viewCount);

		entityManager.flush();
		entityManager.clear();

		// When: 해당 postId로 조회한다.
		Long actualViewCount = backUpRepository.getViewCountByPostId(postId);

		// Then: 저장된 값이 반환된다.
		assertEquals(expectedViewCount, actualViewCount);
	}
}