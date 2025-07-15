package kr.co.mathrank.domain.auth.dto;

import kr.co.mathrank.domain.auth.entity.Member;

public record MemberInfoResult(
	Long memberId,
	String memberName
) {
	public static MemberInfoResult none() {
		return new MemberInfoResult(null, null);
	}

	public static MemberInfoResult from(final Member member) {
		return new MemberInfoResult(member.getId(), member.getName());
	}
}
