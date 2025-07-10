package kr.co.mathrank.domain.auth.repository;

import java.time.Duration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;

@SpringBootTest
class RefreshTokenRepositoryTest {
	@Autowired
	private RefreshTokenRepository refreshTokenRepository;
	private static GenericContainer<?> redisContainer = new GenericContainer<>("redis:6.0.2")
		.withExposedPorts(6379);

	@DynamicPropertySource
	static void init(DynamicPropertyRegistry registry) {
		redisContainer.start();
		registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379));
		registry.add("spring.data.redis.host", () -> redisContainer.getHost());
	}

	@Test
	void 저장된_토큰과_동일하면_TRUE() {
		final Long memberId = 1L;
		final String token = "refreshToken1";
		final Duration duration = Duration.ofSeconds(1L);
		refreshTokenRepository.refresh(memberId, token, duration);

		Assertions.assertTrue(refreshTokenRepository.isValidRefreshToken(memberId, token));
	}

	@Test
	void 저장된_토큰과_다르면_FALSE() {
		final Long memberId = 1L;
		final String token = "refreshToken2";
		final String anotherToken = "anotherRefreshToken1";

		final Duration duration = Duration.ofSeconds(1L);
		refreshTokenRepository.refresh(memberId, token, duration);

		Assertions.assertFalse(refreshTokenRepository.isValidRefreshToken(memberId, anotherToken));
	}

	@Test
	void 저장된게_없으면_FALSE() {
		final Long memberId = 1L;
		final String anotherToken = "anotherRefreshToken2";

		// refreshTokenRepository.refresh(memberId, token, duration);

		Assertions.assertFalse(refreshTokenRepository.isValidRefreshToken(memberId, anotherToken));
	}
}
