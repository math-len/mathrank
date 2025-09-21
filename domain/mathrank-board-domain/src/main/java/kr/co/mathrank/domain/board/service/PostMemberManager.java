package kr.co.mathrank.domain.board.service;

import org.springframework.stereotype.Component;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.client.internal.member.MemberClient;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class PostMemberManager {
	private MemberClient memberClient;

	public String fetchMemberNickName(@NotNull final Long memberId) {
		return memberClient.getMemberInfo(memberId)
			.memberName();
	}
}
