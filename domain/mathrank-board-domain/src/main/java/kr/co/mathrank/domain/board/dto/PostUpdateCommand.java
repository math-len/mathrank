package kr.co.mathrank.domain.board.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record PostUpdateCommand(
	@NotNull
	Long postId,
	@NotNull
	Long memberId,

	@NotEmpty
	String title,
	@NotEmpty
	String content
) {
}
