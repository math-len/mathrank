package kr.co.mathrank.domain.auth.entity;

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
