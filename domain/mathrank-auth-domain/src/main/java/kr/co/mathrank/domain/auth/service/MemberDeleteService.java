package kr.co.mathrank.domain.auth.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.auth.dto.MemberDeleteCommand;
import kr.co.mathrank.domain.auth.entity.Member;
import kr.co.mathrank.domain.auth.exception.CannotFoundMemberException;
import kr.co.mathrank.domain.auth.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class MemberDeleteService {
	private final MemberRepository memberRepository;

	public void delete(@NotNull @Valid final MemberDeleteCommand command) {
		final Member member = getMember(command.targetMemberId());
		memberRepository.delete(member);
		log.info("[MemberDeleteService.delete] member delete success - memberId: {}", command.targetMemberId());
	}

	private Member getMember(final Long targetMemberId) {
		return memberRepository.findById(targetMemberId)
			.orElseThrow(() -> {
				log.info("[MemberDeleteService.getMember] cannot found member - memberId: {}", targetMemberId);
				return new CannotFoundMemberException();
			});
	}
}
