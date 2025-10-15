package kr.co.mathrank.domain.point.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
	indexes = @Index(
		name = "idx_orderId",
		columnList = "order_id",
		unique = true
	)
)
public class PointConsumedLog {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long orderId;

	private Long consumedAmount;

	@ManyToOne(fetch = FetchType.LAZY)
	private UserPoint userPoint;

	static PointConsumedLog of(Long orderId, Long consumedAmount, UserPoint userPoint) {
		final PointConsumedLog pointConsumedLog = new PointConsumedLog();
		pointConsumedLog.orderId = orderId;
		pointConsumedLog.consumedAmount = consumedAmount;
		pointConsumedLog.userPoint = userPoint;

		return pointConsumedLog;
	}
}
