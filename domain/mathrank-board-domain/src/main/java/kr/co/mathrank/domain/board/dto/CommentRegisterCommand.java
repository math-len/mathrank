package kr.co.mathrank.domain.board.dto;

import jakarta.validation.constraints.NotNull;

public record CommentRegisterCommand(
	@NotNull
	Long postId,
	@NotNull
	Long memberId,
	@NotNull
	String content
) {
}
