package kr.co.mathrank.domain.board.viewcount.repository;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;

import lombok.RequiredArgsConstructor;

@Repository
@Validated
@RequiredArgsConstructor
public class ViewCountRepository {
	private final StringRedisTemplate stringRedisTemplate;
	private final ViewCountBackupRepository backUpRepository;

	// math-rank::board::view-count::{boardId}
	private static final String KEY_FORMAT = "math-rank::board::view-count::%s";

	public Long increase(final Long boardId) {
		final String key = getKey(boardId);
		final Long viewCount = stringRedisTemplate.opsForValue().increment(key, 1);

		// redis에 값이 없을 때, 백업 레포를 확인한다.
		if (viewCount == 1L) {
			return fetchAndUpdate(boardId);
		}
		return viewCount;
	}

	public Long get(final Long boardId) {
		final String key = getKey(boardId);
		final String value = stringRedisTemplate.opsForValue().get(key);

		// redis에 값이 없을 때, 백업 레포를 확인한다.
		if (value == null) {
			return fetchAndUpdate(boardId);
		}
		return Long.parseLong(value);
	}

	private Long fetchAndUpdate(final Long boardId) {
		final Long fetchedCount = fetch(boardId);
		return stringRedisTemplate.opsForValue().increment(getKey(boardId), fetchedCount);
	}

	private Long fetch(final Long boardId) {
		final Long viewCount = backUpRepository.getViewCountByPostId(boardId);
		return viewCount == null ? 0L : viewCount;
	}

	private String getKey(final Long boardId) {
		return String.format(KEY_FORMAT, boardId);
	}
}
