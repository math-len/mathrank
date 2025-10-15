package kr.co.mathrank.domain.point.dto;

import jakarta.validation.constraints.NotNull;

public record PointConsumeCommand(
	@NotNull
	Long orderId,
	@NotNull
	Long memberId,
	@NotNull
	Long requirePointAmount
) {
}
