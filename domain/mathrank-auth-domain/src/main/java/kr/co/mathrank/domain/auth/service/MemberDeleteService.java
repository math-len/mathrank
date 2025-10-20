package kr.co.mathrank.domain.auth.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.auth.client.OAuthClientManager;
import kr.co.mathrank.domain.auth.dto.MemberDeleteCommand;
import kr.co.mathrank.domain.auth.entity.Member;
import kr.co.mathrank.domain.auth.exception.CannotFoundMemberException;
import kr.co.mathrank.domain.auth.exception.UnRegisterMemberException;
import kr.co.mathrank.domain.auth.repository.MemberRepository;
import kr.co.mathrank.domain.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class MemberDeleteService {
	private final MemberRepository memberRepository;
	private final OAuthClientManager oAuthClientManager;
	private final RefreshTokenRepository refreshTokenRepository;

	@Transactional
	public void delete(@NotNull @Valid final MemberDeleteCommand command) {
		final Member member = getMember(command.targetMemberId());
		memberRepository.delete(member);

		// oauth 삭제 실패했을 경우
		if (!oAuthClientManager.revoke(member)) {
			log.info("[MemberDeleteService.delete] failed to revoke OAuth client for member - memberId: {}", member.getId());
			throw new UnRegisterMemberException("oauth 연동 해제중 에러 발생했습니다. 잠시 후 다시 시도해주세요");
		}
		refreshTokenRepository.expire(member.getId());
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
