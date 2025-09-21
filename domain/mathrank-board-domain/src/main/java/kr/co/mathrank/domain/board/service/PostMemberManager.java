package kr.co.mathrank.domain.board.service;

import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.client.internal.member.MemberClient;
import lombok.RequiredArgsConstructor;

@Component
@Validated
@RequiredArgsConstructor
class PostMemberManager {
	private final MemberClient memberClient;

	public String fetchMemberNickName(@NotNull final Long memberId) {
		return memberClient.getMemberInfo(memberId)
			.memberName();
	}
}
