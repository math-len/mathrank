package kr.co.mathrank.domain.board.viewcount.repository;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@Repository
@Validated
@RequiredArgsConstructor
public class ViewCountLockRepository {
	private final StringRedisTemplate stringRedisTemplate;

	// math-rank::board::view-count::lock::{boardId}::{requestMemberId}"
	private static final String KEY_FORMAT = "math-rank::board::view-count::lock::%s::%s";

	public boolean lock(@NotNull final Long boardId, @NotNull final Long requestMemberId, @NotNull final Duration duration) {
		final String key = getKey(boardId, requestMemberId);
		return stringRedisTemplate.opsForValue().setIfAbsent(key, "", duration);
	}

	private String getKey(final Long boardId, final Long requestMemberId) {
		return String.format(KEY_FORMAT, boardId, requestMemberId);
	}
}
