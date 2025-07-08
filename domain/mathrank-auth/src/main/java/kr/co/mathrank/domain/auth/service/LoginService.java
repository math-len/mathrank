package kr.co.mathrank.domain.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.auth.dto.JwtLoginResult;
import kr.co.mathrank.domain.auth.dto.LoginCommand;
import kr.co.mathrank.domain.auth.entity.Member;
import kr.co.mathrank.domain.auth.entity.Password;
import kr.co.mathrank.domain.auth.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class LoginService {
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	private final JwtLoginManager jwtLoginManager;

	public JwtLoginResult login(@NotNull @Valid final LoginCommand command) {
		final Member member = memberRepository.findByLoginId(command.loginId())
			.orElseThrow();

		// 비밀번호 일치
		if (isMatch(command.password(), member.getPassword())) {
			return jwtLoginManager.login(member.getId(), member.getRole());
		}

		log.warn("[LoginService.login] password not matched");
		throw new IllegalArgumentException();
	}

	private boolean isMatch(final Password password, final String encryptedPassword) {
		return password.matches(encryptedPassword, passwordEncoder);
	}
}
