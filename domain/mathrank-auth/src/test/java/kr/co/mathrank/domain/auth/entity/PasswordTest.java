package kr.co.mathrank.domain.auth.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

class PasswordTest {
	private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Test
	void 같은_비밀번호일때_TRUE() {
		final Password password = new Password("test");
		final String encryptedPassword = password.encrypt(passwordEncoder);

		Assertions.assertTrue(password.matches(encryptedPassword, passwordEncoder));
	}

	@Test
	void 다른_비밀번호일때_FALSE() {
		final Password password = new Password("test");
		final String encryptedPassword = password.encrypt(passwordEncoder);

		final Password differentPassword = new Password("different");

		Assertions.assertFalse(differentPassword.matches(encryptedPassword, passwordEncoder));
	}
}
