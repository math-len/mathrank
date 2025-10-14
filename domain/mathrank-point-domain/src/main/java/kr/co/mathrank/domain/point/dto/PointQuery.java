package kr.co.mathrank.domain.point.dto;

import jakarta.validation.constraints.NotNull;

public record PointQuery(
	@NotNull
	Long targetMemberId
) {
}
