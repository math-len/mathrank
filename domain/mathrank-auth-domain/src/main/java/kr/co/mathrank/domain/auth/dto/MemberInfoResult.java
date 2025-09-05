package kr.co.mathrank.domain.auth.dto;

import java.time.LocalDateTime;

import kr.co.mathrank.common.role.Role;
import kr.co.mathrank.domain.auth.entity.Member;
import kr.co.mathrank.domain.auth.entity.MemberType;

public record MemberInfoResult(
	Long memberId,
	String nickName,
	Role role,
	MemberType memberType,
	LocalDateTime createdAt,
	Boolean agreeToPrivacyPolicy,
	Boolean pending,
	String schoolCode
) {
	public static MemberInfoResult from(final Member member) {
		return new MemberInfoResult(
			member.getId(),
			member.getName(),
			member.getRole(),
			member.getMemberType(),
			member.getCreatedAt(),
			member.getAgreeToPrivacyPolicy(),
			member.getPending(),
			member.getSchoolCode()
		);
	}

	public static MemberInfoResult none() {
		return new MemberInfoResult(
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null
		);
	}
}
