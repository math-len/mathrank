package kr.co.mathrank.domain.point.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
	indexes = {
		@Index(
			name = "idx_pointChargedLog_paymentId",
			columnList = "payment_id, charged"
		)
	}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointChargedLog {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String paymentId;

	private Long purchasedAmount;

	private Long chargedAmount;

	private Long memberId; // 충전한 사용자

	private Boolean charged; // 충전 여부

	private String reason; // 실패 이유

	@CreationTimestamp
	private LocalDateTime createdAt;

	public static PointChargedLog failed(final String paymentId, final Long purchasedAmount, final Long chargedAmount, final Long memberId, final String reason) {
		final PointChargedLog purchasedLog = new PointChargedLog();
		purchasedLog.purchasedAmount = purchasedAmount;
		purchasedLog.chargedAmount = chargedAmount;
		purchasedLog.paymentId = paymentId;
		purchasedLog.memberId = memberId;
		purchasedLog.reason = reason;
		purchasedLog.charged = false;

		return purchasedLog;
	}

	public static PointChargedLog succeeded(final String paymentId, final Long purchasedAmount, final Long chargedAmount, final Long memberId) {
		final PointChargedLog purchasedLog = new PointChargedLog();
		purchasedLog.purchasedAmount = purchasedAmount;
		purchasedLog.chargedAmount = chargedAmount;
		purchasedLog.paymentId = paymentId;
		purchasedLog.memberId = memberId;
		purchasedLog.charged = true;

		return purchasedLog;
	}
}
