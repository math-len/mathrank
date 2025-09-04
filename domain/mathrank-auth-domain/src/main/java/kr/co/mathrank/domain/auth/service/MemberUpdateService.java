package kr.co.mathrank.domain.auth.service;

import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.auth.dto.MemberUpdateCommand;
import kr.co.mathrank.domain.auth.entity.Member;
import kr.co.mathrank.domain.auth.exception.CannotFoundMemberException;
import kr.co.mathrank.domain.auth.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class MemberUpdateService {
	private final MemberRepository memberRepository;

	public void update(@NotNull @Valid final MemberUpdateCommand command) {
		final Member member = memberRepository.findById(command.memberId())
			.orElseThrow(() -> {
				log.info("[MemberUpdateService.update] cannot found member - memberId: {}", command.memberId());
				return new CannotFoundMemberException();
			});

		member.setMemberType(command.memberType());
		member.setSchoolCode(command.schoolCode());
		member.setAgreeToPrivacyPolicy(command.agreeToPolicy());
		member.setName(command.userNickName());

		memberRepository.save(member);
		log.info("[MemberUpdateService.update] member updated - memberId: {}", member.getId());
	}
}
