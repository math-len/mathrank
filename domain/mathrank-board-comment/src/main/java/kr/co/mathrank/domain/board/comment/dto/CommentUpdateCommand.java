package kr.co.mathrank.domain.board.comment.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;

public record CommentUpdateCommand(
	@NotNull
	Long commentId,
	@NotNull
	Long userId,
	@NotNull
	String content,
	@NotNull
	List<String> images
) {
}
