package kr.co.mathrank.domain.auth.dto;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.auth.entity.Password;

public record LoginCommand(
	@NotNull
	String loginId,
	@NotNull
	Password password
) {
}
