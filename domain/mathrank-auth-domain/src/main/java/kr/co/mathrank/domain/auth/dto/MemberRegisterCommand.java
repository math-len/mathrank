package kr.co.mathrank.domain.auth.dto;

import java.util.Set;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.role.Role;
import kr.co.mathrank.domain.auth.entity.MemberType;
import kr.co.mathrank.domain.auth.entity.Password;

public record MemberRegisterCommand(
	@NotNull
	String loginId,
	@NotNull
	String name,
	@NotNull
	Password password,
	@NotNull
	Role role,
	@NotNull
	MemberType memberType,
	@NotNull
	Boolean agreeToPrivacyPolicy,
	@NotNull
	Set<String> schoolCodes
) {
}
