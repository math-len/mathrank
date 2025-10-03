package kr.co.mathrank.domain.point.dto;

import jakarta.validation.constraints.NotNull;

public record PointProductDeleteCommand(
	@NotNull
	Long pointProductId
) {
}
