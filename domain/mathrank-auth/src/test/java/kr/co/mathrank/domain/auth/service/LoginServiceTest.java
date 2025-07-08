package kr.co.mathrank.domain.auth.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;

import kr.co.mathrank.common.role.Role;
import kr.co.mathrank.domain.auth.dto.LoginCommand;
import kr.co.mathrank.domain.auth.dto.MemberRegisterCommand;
import kr.co.mathrank.domain.auth.entity.Password;

@SpringBootTest
@Transactional
class LoginServiceTest {
	@Autowired
	private LoginService loginService;
	@Autowired
	private MemberRegisterService memberRegisterService;

	private static GenericContainer<?> redisContainer = new GenericContainer<>("redis:6.0.2")
		.withExposedPorts(6379);

	@DynamicPropertySource
	static void init(DynamicPropertyRegistry registry) {
		redisContainer.start();
		registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379));
		registry.add("spring.data.redis.host", () -> redisContainer.getHost());
	}

	@Test
	void 비밀번호_일치시_로그인_성공() {
		final String loginId = "loginId";
		final Password password = new Password("test");

		memberRegisterService.register(new MemberRegisterCommand(loginId, password, Role.USER));
		Assertions.assertNotNull(loginService.login(new LoginCommand(loginId, password)));
	}

	@Test
	void 비밀번호_비일치시_로그인_실패() {
		final String loginId = "loginId";
		final Password password = new Password("test");
		final Password wrongPassword = new Password("wrong");

		memberRegisterService.register(new MemberRegisterCommand(loginId, password, Role.USER));
		Assertions.assertThrows(IllegalArgumentException.class, () -> loginService.login(new LoginCommand(loginId, wrongPassword)));
	}
}
