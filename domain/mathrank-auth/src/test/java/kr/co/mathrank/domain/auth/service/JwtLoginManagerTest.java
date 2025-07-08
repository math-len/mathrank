package kr.co.mathrank.domain.auth.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;

import kr.co.mathrank.common.role.Role;
import kr.co.mathrank.domain.auth.dto.JwtLoginResult;

@SpringBootTest
class JwtLoginManagerTest {
	@Autowired
	private JwtLoginManager jwtLoginManager;

	private static GenericContainer<?> redisContainer = new GenericContainer<>("redis:6.0.2")
		.withExposedPorts(6379);

	@DynamicPropertySource
	static void init(DynamicPropertyRegistry registry) {
		redisContainer.start();
		registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379));
		registry.add("spring.data.redis.host", () -> redisContainer.getHost());
	}

	@Test
	void 최신_refresh_토큰이_아니면_사용_불가능하다() {
		final JwtLoginResult result = jwtLoginManager.login(1L, Role.USER);
		final JwtLoginResult anotherLoginResult = jwtLoginManager.login(1L, Role.USER);

		Assertions.assertThrows(IllegalArgumentException.class, () -> jwtLoginManager.refresh(result.refreshToken()));
	}

	@Test
	void 최신_refreshToken으론_성공한다() {
		final JwtLoginResult result = jwtLoginManager.login(1L, Role.USER);

		Assertions.assertDoesNotThrow(() -> jwtLoginManager.refresh(result.refreshToken()));
	}
}
