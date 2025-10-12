package kr.co.mathrank.domain.contents.dto;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.role.Role;

public record ContentReadQuery(
	@NotNull
	Long contentId,
	@NotNull
	Long userId,
	@NotNull
	Role role
) {
}
