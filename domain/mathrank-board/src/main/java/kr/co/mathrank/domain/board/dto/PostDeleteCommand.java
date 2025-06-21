package kr.co.mathrank.domain.board.dto;

import jakarta.validation.constraints.NotNull;

public record PostDeleteCommand(
	@NotNull
	Long requestMemberId,
	@NotNull
	String postId
) {
}
