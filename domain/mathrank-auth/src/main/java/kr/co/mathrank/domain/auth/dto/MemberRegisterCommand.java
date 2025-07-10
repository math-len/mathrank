package kr.co.mathrank.domain.auth.dto;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.role.Role;
import kr.co.mathrank.domain.auth.entity.Password;

public record MemberRegisterCommand(
	@NotNull
	String loginId,
	@NotNull
	Password password,
	@NotNull
	Role role
) {
}
