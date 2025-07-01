package kr.co.mathrank.domain.board.viewcount.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.board.viewcount.entity.ViewCount;
import kr.co.mathrank.domain.board.viewcount.repository.ViewCountBackupRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
class ViewCountBackupManager {
	private final ViewCountBackupRepository viewCountBackupRepository;

	public boolean tryBackup(@NotNull final Long postId, @NotNull final Long viewCount) {
		// 백업은 조회수가 100의 배수일 때만 수행
		if (viewCount % 100 == 0) {
			backup(postId, viewCount);
			return true;
		}
		return false;
	}

	public void backup(final Long postId, final Long viewCount) {
		final int appliedCount = viewCountBackupRepository.updateViewCountIfLessThan(postId, viewCount);

		// 업데이트된 레코드가 없으면, 백업 레포에 저장시도
		// 존재 O -> continue
		// 존재 X -> save
		if (appliedCount == 0) {
			viewCountBackupRepository.findByPostId(postId).ifPresentOrElse(
				boardViewCount -> {},
				() -> viewCountBackupRepository.save(ViewCount.of(postId, viewCount)));
		}
	}
}
