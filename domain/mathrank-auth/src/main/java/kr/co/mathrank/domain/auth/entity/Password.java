package kr.co.mathrank.domain.auth.entity;

import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class Password {
	private final String rawPassword;

	public String encrypt(final PasswordEncoder passwordEncoder) {
		return passwordEncoder.encode(rawPassword);
	}

	public boolean matches(final String encryptedPassword, final PasswordEncoder passwordEncoder) {
		return passwordEncoder.matches(rawPassword, encryptedPassword);
	}
}
