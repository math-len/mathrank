package kr.co.mathrank.domain.point.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@Table(
	indexes = {
		@Index(name = "idx_unique_userId", columnList = "user_id", unique = true)
	}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserPoint {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long userId;

	private Long pointAmount = 0L;

	@Version
	private Long version = 0L;

	public static UserPoint of(Long userId) {
		final UserPoint userPoint = new UserPoint();
		userPoint.userId = userId;

		return userPoint;
	}

	public void addPoint(Long pointAmount) {
		this.pointAmount += pointAmount;
	}

	/**
	 * 포인트 소모 해도 0 이상인지 확인한다.
	 * @param pointAmount
	 * @return
	 */
	public boolean canRemovePoint(Long pointAmount) {
		return this.pointAmount - pointAmount >= 0;
	}

	public void removePoint(Long pointAmount) {
		this.pointAmount -= pointAmount;

		if (this.pointAmount < 0) {
			throw new IllegalStateException("cannot set negative point");
		}
	}
}
