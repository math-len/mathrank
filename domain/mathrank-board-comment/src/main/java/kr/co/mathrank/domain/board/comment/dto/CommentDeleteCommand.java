package kr.co.mathrank.domain.board.comment.dto;

import jakarta.validation.constraints.NotNull;

public record CommentDeleteCommand(
	@NotNull
	Long commentId,
	@NotNull
	Long requestMemberId
) {
}
