package kr.co.mathrank.domain.board.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.board.constraint.PostContentConstraint;
import kr.co.mathrank.domain.board.constraint.PostImagesConstraint;
import kr.co.mathrank.domain.board.constraint.PostTitleConstraint;

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
