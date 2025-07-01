package kr.co.mathrank.domain.board.comment.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;

public record CommentRegisterCommand(
	@NotNull
	Long postId,
	@NotNull
	Long userId,
	@NotNull
	String content,
	@NotNull
	List<String> images
) {
}
