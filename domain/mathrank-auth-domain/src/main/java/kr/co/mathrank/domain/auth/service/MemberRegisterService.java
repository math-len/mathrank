package kr.co.mathrank.domain.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.snowflake.Snowflake;
import kr.co.mathrank.domain.auth.dto.MemberRegisterCommand;
import kr.co.mathrank.domain.auth.entity.Member;
import kr.co.mathrank.domain.auth.exception.AuthException;
import kr.co.mathrank.domain.auth.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class MemberRegisterService {
	private final Snowflake snowflake;
	private final PasswordEncoder passwordEncoder;
	private final MemberRepository memberRepository;

	@Transactional
	public Long register(@NotNull @Valid final MemberRegisterCommand command) {
		if (isExist(command.loginId())) {
			log.warn("[MemberRegisterService.register] 이미 존재하는 loginId: {}", command.loginId());
			throw new AuthException();
		}

		final Member newMember = Member.of(snowflake.nextId(), command.role(), command.loginId(),
			command.password().encrypt(passwordEncoder));

		return memberRepository.save(newMember).getId();
	}

	private boolean isExist(final String loginId) {
		return memberRepository.findByLoginId(loginId).isPresent();
	}
}
