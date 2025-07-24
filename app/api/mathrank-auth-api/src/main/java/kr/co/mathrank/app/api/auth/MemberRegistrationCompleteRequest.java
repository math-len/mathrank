package kr.co.mathrank.app.api.auth;

import java.util.Set;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.auth.dto.MemberInfoCompleteCommand;
import kr.co.mathrank.domain.auth.entity.MemberType;

public record MemberRegistrationCompleteRequest(
	@NotNull
	MemberType memberType,
	@NotNull
	Set<String> schoolCodes,
	@NotNull
	Boolean agreeToPrivacyPolicy
) {
	public MemberInfoCompleteCommand toCommand(final Long memberId) {
		return new MemberInfoCompleteCommand(memberId, memberType, schoolCodes, agreeToPrivacyPolicy);
	}
}
