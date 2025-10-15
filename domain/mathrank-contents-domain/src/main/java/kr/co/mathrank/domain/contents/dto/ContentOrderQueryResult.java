package kr.co.mathrank.domain.contents.dto;

import java.time.LocalDateTime;

import kr.co.mathrank.domain.contents.entity.ContentOrder;
import kr.co.mathrank.domain.contents.entity.OrderStatus;

public record ContentOrderQueryResult(
	Long id,
	Long userId,
	Long contentId,
	String idempotencyKey,
	OrderStatus orderStatus,
	Long purchasedPointAmount,
	LocalDateTime createdAt,
	LocalDateTime completedAt
) {
	public static ContentOrderQueryResult from(final ContentOrder order) {
		return new ContentOrderQueryResult(
			order.getId(),
			order.getUserId(),
			order.getContent().getId(),
			order.getIdempotencyKey(),
			order.getOrderStatus(),
			order.getPurchasedPointAmount(),
			order.getCreatedAt(),
			order.getCompletedAt()
		);
	}
}
