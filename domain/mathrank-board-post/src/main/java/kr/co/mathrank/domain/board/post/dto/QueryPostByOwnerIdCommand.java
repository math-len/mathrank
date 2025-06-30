package kr.co.mathrank.domain.board.post.dto;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.board.post.entity.BoardCategory;

public record QueryPostByOwnerIdCommand(
	@NotNull
	Long ownerId,
	@NotNull
	BoardCategory boardCategory,
	@NotNull
	Integer pageSize
) {
}
