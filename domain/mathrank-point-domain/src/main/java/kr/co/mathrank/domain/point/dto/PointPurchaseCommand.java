package kr.co.mathrank.domain.point.dto;

import jakarta.validation.constraints.NotNull;

public record PointPurchaseCommand(
	@NotNull
	String paymentId,
	@NotNull
	Long requestMemberId
) {
}
