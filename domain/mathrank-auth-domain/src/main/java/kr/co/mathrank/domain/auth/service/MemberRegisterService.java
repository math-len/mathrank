package kr.co.mathrank.domain.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.snowflake.Snowflake;
import kr.co.mathrank.domain.auth.dto.MemberInfoCompleteCommand;
import kr.co.mathrank.domain.auth.dto.MemberRegisterCommand;
import kr.co.mathrank.domain.auth.entity.Member;
import kr.co.mathrank.domain.auth.exception.AlreadyExistLoginIdException;
import kr.co.mathrank.domain.auth.exception.CannotFoundMemberException;
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
			throw new AlreadyExistLoginIdException();
		}

		final Member newMember = Member.of(snowflake.nextId(), command.name(), command.role(), command.loginId(),
			command.password().encrypt(passwordEncoder));
		memberRepository.save(newMember);

		log.info("[MemberRegisterService.register] member saved - memberId: {}", newMember.getId());
		return newMember.getId();
	}

	@Transactional
	public void completeRegister(@NotNull @Valid final MemberInfoCompleteCommand command) {
		memberRepository.findById(command.memberId())
			.ifPresentOrElse(member -> {
				// 사용자 존재 시, 업데이트 마무리
					member.setMemberType(command.memberType());
					member.setAgreeToPrivacyPolicy(command.agreeToPrivacyPolicy());
					member.setSchools(command.schoolCodes());
				},
				() -> {
				// 사용자를 찾을 수 없을 때, 예외처리
					log.warn("[MemberRegisterService.completeRegister] cannot find member: {}", command.memberId());
					throw new CannotFoundMemberException();
				});
		log.info("[MemberRegisterService.completeRegister] member register completed - memberId: {}", command.memberId());
	}

	private boolean isExist(final String loginId) {
		return memberRepository.findByLoginId(loginId).isPresent();
	}
}
