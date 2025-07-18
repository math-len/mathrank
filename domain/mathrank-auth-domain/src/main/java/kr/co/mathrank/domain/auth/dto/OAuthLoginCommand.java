package kr.co.mathrank.domain.auth.dto;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.auth.entity.OAuthProvider;

public record OAuthLoginCommand(
	@NotNull
	String code,
	String state,
	@NotNull
	OAuthProvider provider
) {
}
