package kr.co.mathrank.domain.auth.repository;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {
	private final StringRedisTemplate redisTemplate;

	// math-rank::auth::jwt::refresh-token::{memberId}
	private static final String KEY_FORMAT = "math-rank::auth::jwt::refresh-token::%s";

	public void refresh(final Long memberId, final String refreshToken, final Duration expiration) {
		final String key = getKey(memberId);
		redisTemplate.opsForValue().set(key, refreshToken, expiration);
	}

	public void expire(final Long memberId) {
		final String key = getKey(memberId);
		redisTemplate.delete(key);
	}

	public boolean isValidRefreshToken(final Long memberId, final String refreshToken) {
		final String key = getKey(memberId);
		final String storedToken = redisTemplate.opsForValue().get(key);

		// 로그아웃된 사용자
		if (storedToken == null) {
			return false;
		}

		// 최근 로그인 사용자인가?
		return storedToken.equals(refreshToken);
	}

	private String getKey(final Long memberId) {
		return KEY_FORMAT.formatted(memberId);
	}
}

