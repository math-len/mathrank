package kr.co.mathrank.domain.contents.dto;

import javax.management.relation.Role;

import jakarta.validation.constraints.NotNull;

public record ContentReadQuery(
	@NotNull
	Long contentId,
	@NotNull
	Long userId,
	@NotNull
	Role role
) {
}
