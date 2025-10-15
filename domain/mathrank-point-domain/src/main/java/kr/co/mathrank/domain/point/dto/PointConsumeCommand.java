package kr.co.mathrank.domain.point.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;

public record PointConsumeCommand(
	@NotNull
	Long memberId,
	@NotNull
	Long requirePointAmount
) {
}
