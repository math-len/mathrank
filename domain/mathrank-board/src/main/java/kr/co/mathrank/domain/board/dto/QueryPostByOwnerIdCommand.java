package kr.co.mathrank.domain.board.dto;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.board.entity.BoardCategory;

public record QueryPostByOwnerIdCommand(
	@NotNull
	Long ownerId,
	@NotNull
	BoardCategory boardCategory,
	@NotNull
	Integer pageSize
) {
}
