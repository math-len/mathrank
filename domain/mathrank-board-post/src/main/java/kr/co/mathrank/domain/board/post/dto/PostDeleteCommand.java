package kr.co.mathrank.domain.board.post.dto;

import jakarta.validation.constraints.NotNull;

public record PostDeleteCommand(
	@NotNull
	Long requestMemberId,
	@NotNull
	String postId
) {
}
