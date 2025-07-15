package kr.co.mathrank.app.api.problem.read;

import kr.co.mathrank.client.internal.member.MemberInfo;

public record MemberResponse(
	Long memberId,
	String memberName
) {
	public static MemberResponse from(final MemberInfo memberInfo) {
		return new MemberResponse(
			memberInfo.memberId(),
			memberInfo.memberName()
		);
	}
}
