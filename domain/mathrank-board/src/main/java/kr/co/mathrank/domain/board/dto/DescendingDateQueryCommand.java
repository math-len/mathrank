package kr.co.mathrank.domain.board.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

public record DescendingDateQueryCommand(
	@NotNull
	LocalDateTime current,
	@NotNull
	Integer pageSize
) {
}
