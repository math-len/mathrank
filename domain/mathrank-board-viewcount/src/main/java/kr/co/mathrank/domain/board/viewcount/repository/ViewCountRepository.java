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

	// math-rank::board::view-count::{boardId}
	private static final String KEY_FORMAT = "math-rank::board::view-count::%s";

	public Long increase(final Long boardId) {
		final String key = getKey(boardId);
		return stringRedisTemplate.opsForValue().increment(key, 1);
	}

	public Long get(final Long boardId) {
		final String key = getKey(boardId);
		final String value = stringRedisTemplate.opsForValue().get(key);
		return value == null ? 0L : Long.parseLong(value);
	}

	private String getKey(final Long boardId) {
		return String.format(KEY_FORMAT, boardId);
	}
}
