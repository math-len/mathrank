package kr.co.mathrank.domain.board.dto;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.role.Role;

public record PostDeleteCommand(
	@NotNull
	Long postId,
	@NotNull
	Long memberId,
	@NotNull
	Role role
) {
}
