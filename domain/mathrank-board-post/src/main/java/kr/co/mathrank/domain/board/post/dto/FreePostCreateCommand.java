package kr.co.mathrank.domain.board.post.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.board.post.constraint.PostContentConstraint;
import kr.co.mathrank.domain.board.post.constraint.PostImagesConstraint;
import kr.co.mathrank.domain.board.post.constraint.PostTitleConstraint;

public record FreePostCreateCommand(
	@NotNull
	Long memberId,
	@PostTitleConstraint
	String title,
	@PostContentConstraint
	String content,
	@PostImagesConstraint
	List<String> images
) {
}
