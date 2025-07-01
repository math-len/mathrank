package kr.co.mathrank.domain.board.viewcount.service;

import java.time.Duration;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.board.viewcount.repository.ViewCountLockRepository;
import kr.co.mathrank.domain.board.viewcount.repository.ViewCountRepository;
import lombok.RequiredArgsConstructor;

@Service
@Validated
@RequiredArgsConstructor
public class ViewCountService {
	private final ViewCountLockRepository viewCountLockRepository;
	private final ViewCountRepository viewCountRepository;
	private final ViewCountBackupManager viewCountBackupManager;

	private final Duration lockDuration = Duration.ofDays(1L);

	public Long increase(@NotNull final Long boardId, @NotNull final Long requestMemberId) {
		if (viewCountLockRepository.lock(boardId, requestMemberId, lockDuration)) {
			final Long viewCount = viewCountRepository.increase(boardId);
			viewCountBackupManager.tryBackup(boardId, viewCount);
		}
		return viewCountRepository.get(boardId);
	}

	public Long get(@NotNull final Long boardId) {
		return viewCountRepository.get(boardId);
	}
}
