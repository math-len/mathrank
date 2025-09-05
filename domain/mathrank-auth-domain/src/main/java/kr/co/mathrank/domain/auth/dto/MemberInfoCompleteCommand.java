package kr.co.mathrank.domain.auth.dto;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.auth.entity.MemberType;

public record MemberInfoCompleteCommand(
	@NotNull
	Long memberId,
	@NotNull
	MemberType memberType,
	String schoolCode,
	@NotNull
	Boolean agreeToPrivacyPolicy
) {
}
