package kr.co.mathrank.domain.problem.read.dto;

import kr.co.mathrank.client.internal.member.MemberInfo;

public record MemberResponse(
	String memberId,
	String memberName
) {
	public static MemberResponse from(final MemberInfo memberInfo) {
		return new MemberResponse(
			String.valueOf(memberInfo.memberId()),
			memberInfo.memberName()
		);
	}
}
