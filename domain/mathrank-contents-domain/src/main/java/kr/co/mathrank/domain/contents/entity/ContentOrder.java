package kr.co.mathrank.domain.contents.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(
	indexes = {
		@Index(
			name = "idx_unique_idempotencyKey",
			columnList = "idempotency_key",
			unique = true
		)
	}
)
public class ContentOrder {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long userId;

	@ManyToOne(fetch = FetchType.LAZY)
	private Content content;

	private String idempotencyKey;

	private OrderStatus orderStatus;

	private Long purchasedPointAmount;

	@CreationTimestamp
	private LocalDateTime createdAt;

	private LocalDateTime purchasedAt;

	public static ContentOrder create(
		Content content,
		Long userId,
		String idempotencyKey
	) {
		final ContentOrder order = new ContentOrder();
		order.content = content;
		order.userId = userId;
		order.idempotencyKey = idempotencyKey;
		order.orderStatus = OrderStatus.PENDING;
		order.purchasedPointAmount = content.getPrice().longValue();

		return order;
	}
}
