package kr.co.mathrank.domain.board.viewcount.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.co.mathrank.domain.board.viewcount.repository.ViewCountBackupRepository;

@SpringBootTest
@Transactional
class ViewCountBackupManagerTest {
	@Autowired
	private ViewCountBackupManager viewCountBackupManager;
	@Autowired
	private ViewCountBackupRepository viewCountBackupRepository;

	@Test
	void 조회수가_100단위일때_저장된다() {
		final Long postId = 1L;
		final Long viewCount = 100L;

		viewCountBackupManager.tryBackup(postId, viewCount);

		Assertions.assertEquals(1, viewCountBackupRepository.count());
	}

	@Test
	void 백업된_값보다_클때만_저장된다() {
		viewCountBackupManager.tryBackup(1L, 1000L);
		viewCountBackupManager.tryBackup(1L, 100L);

		Assertions.assertEquals(1000L, viewCountBackupRepository.getViewCountByPostId(1L));
	}
}