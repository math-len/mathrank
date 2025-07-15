package kr.co.mathrank.domain.auth.entity;

import java.time.Duration;
import java.time.LocalDateTime;

import jakarta.persistence.Embeddable;

@Embeddable
public class LockInfo {
	private static final int MAX_TRY_COUNT = 3;
	private static final int LOCK_MINUTES = 5;

	private Integer loginTryCount = 0;
	private LocalDateTime lockStartedAt;

	public boolean isLocked(final LocalDateTime now) {
		if (this.lockStartedAt == null) {
			return false;
		}

		return lockStartedAt.plusMinutes(LOCK_MINUTES).isAfter(now);
	}

	public Duration getRemainLockDuration(final LocalDateTime now) {
		// 잠금되지 않음
		if (lockStartedAt == null) {
			return Duration.ZERO;
		}

		// 아직 잠금 중임
		if (isLocked(now)) {
			return Duration.between(now, lockStartedAt.plusMinutes(LOCK_MINUTES));
		}

		// 잠금 끝남
		return Duration.ZERO;
	}

	public void addFailedCount(final LocalDateTime now) {
		this.loginTryCount++;
		if (loginTryCount >= MAX_TRY_COUNT) {
			lock(now);
		}
	}

	public void unlock() {
		this.loginTryCount = 0;
		this.lockStartedAt = null;
	}

	public int getRemainTryCount() {
		final int diff = MAX_TRY_COUNT - this.loginTryCount;
		return Math.max(0, diff);
	}

	private void lock(final LocalDateTime now) {
		this.lockStartedAt = now;
	}
}
