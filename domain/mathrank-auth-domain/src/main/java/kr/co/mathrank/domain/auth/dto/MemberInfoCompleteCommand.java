package kr.co.mathrank.domain.auth.dto;

import java.util.Set;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.auth.entity.MemberType;

public record MemberInfoCompleteCommand(
	@NotNull
	Long memberId,
	@NotNull
	MemberType memberType,
	@NotNull
	Set<String> schoolCodes,
	@NotNull
	Boolean agreeToPrivacyPolicy
) {
}
