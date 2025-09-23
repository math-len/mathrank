package kr.co.mathrank.domain.board.dto;

import jakarta.validation.constraints.NotNull;

public record CommentUpdateCommand(
	@NotNull
	Long commentId,
	@NotNull
	Long memberId,
	@NotNull
	String content
) {
}
