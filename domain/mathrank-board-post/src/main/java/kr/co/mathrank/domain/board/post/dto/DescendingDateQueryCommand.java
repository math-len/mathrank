package kr.co.mathrank.domain.board.post.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.board.post.entity.BoardCategory;

public record DescendingDateQueryCommand(
	@NotNull
	BoardCategory category,
	@NotNull
	LocalDateTime current,
	@NotNull
	Integer pageSize
) {
}
