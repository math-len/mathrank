package kr.co.mathrank.domain.auth.service;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.auth.dto.JwtLoginResult;
import kr.co.mathrank.domain.auth.dto.LoginCommand;
import kr.co.mathrank.domain.auth.entity.Member;
import kr.co.mathrank.domain.auth.entity.Password;
import kr.co.mathrank.domain.auth.exception.AuthException;
import kr.co.mathrank.domain.auth.exception.MemberLockedException;
import kr.co.mathrank.domain.auth.exception.PasswordMismatchedException;
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

	@Transactional(noRollbackFor = IllegalArgumentException.class)
	public JwtLoginResult login(@NotNull @Valid final LoginCommand command) {
		final Member member = memberRepository.findByLoginId(command.loginId())
			.orElseThrow(AuthException::new);

		final LocalDateTime now = LocalDateTime.now();
		// lock 인지 확인
		if (member.getLockInfo().isLocked(now)) {
			log.warn("[LoginService.login] cannot login with locked member: {}", member.getId());
			throw new MemberLockedException();
		}

		// 비밀번호 일치
		if (isMatch(command.password(), member.getPassword())) {
			member.getLockInfo().unlock();
			return jwtLoginManager.login(member.getId(), member.getRole());
		}

		// 비밀번호 불일치
		member.getLockInfo().addFailedCount(now);
		log.warn("[LoginService.login] password not matched");
		throw new PasswordMismatchedException();
	}

	private boolean isMatch(final Password password, final String encryptedPassword) {
		return password.matches(encryptedPassword, passwordEncoder);
	}
}
