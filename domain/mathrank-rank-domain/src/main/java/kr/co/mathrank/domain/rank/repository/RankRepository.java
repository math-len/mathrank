package kr.co.mathrank.domain.rank.repository;

import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@Component
@Validated
@RequiredArgsConstructor
public class RankRepository {
	private static final String KEY = "mathrank::domain::rank";

	private final RedisTemplate<String, String> redisTemplate;

	/**
	 *
	 * @param userId
	 * @return
	 * 동점일 경우, 같은 등수를 리턴합니다.
	 *
	 * ex)
	 * member	- 	score
	 * 1 		-	100
	 * 2		-	200
	 * 3		- 	200
	 *
	 * 1 사용자 랭크 조회 시, 3등
	 * 2, 3 사용자 조회 시, 1등
	 */
	public Long getRank(@NotNull final String userId) {
		// O(1)
		final Long userScore = getScore(userId);
		if (userScore == null) {
			return null;
		}
		// O(logN)
		return redisTemplate.opsForZSet().count(KEY, userScore + 1, Double.MAX_VALUE) + 1;
	}

	public Long getScore(@NotNull final String userId) {
		final Double userScore = redisTemplate.opsForZSet().score(KEY, userId);
		return userScore == null ? null : userScore.longValue();
	}

	public void set(@NotNull final String userId, @NotNull final Long score) {
		redisTemplate.opsForZSet().add(KEY, userId, score);
	}

	/**
	 * 기존에 없던 값이거나 저장된 값보다 클때만 업데이트 합니다.
	 * @param userId
	 * @param score
	 */
	public void setIfGreaterThan(@NotNull final String userId, @NotNull final Long score) {
		redisTemplate.execute((RedisCallback<?>) connection -> {
			final StringRedisConnection redisConnection = (StringRedisConnection) connection;
			redisConnection.zAdd(KEY, score, userId, RedisZSetCommands.ZAddArgs.empty().gt());
			return null;
		});
	}

	public Long getTotalRankerCount() {
		return redisTemplate.opsForZSet().size(KEY);
	}

	public void clear() {
		redisTemplate.delete(KEY);
	}
}
