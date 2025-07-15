package kr.co.mathrank.domain.auth.entity;

import java.time.Duration;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LockInfoTest {

	@Test
	void 세번_실패하면_잠긴다() {
		final LockInfo lockInfo = new LockInfo();
		final LocalDateTime now = LocalDateTime.now();
		for (int i = 0; i < 3; i++) {
			lockInfo.addFailedCount(now);
		}

		Assertions.assertTrue(lockInfo.isLocked(now));
	}

	@Test
	void 잠긴후_5분이자나면_해제된다() {
		final LockInfo lockInfo = new LockInfo();
		final LocalDateTime now = LocalDateTime.now();
		for (int i = 0; i < 3; i++) {
			lockInfo.addFailedCount(now);
		}

		Assertions.assertFalse(lockInfo.isLocked(now.plusMinutes(5L)));
	}

	@Test
	void 시간이_지나서_잠금이_풀려도_한번더_틀리면_바로잠금() {
		final LockInfo lockInfo = new LockInfo();
		final LocalDateTime now = LocalDateTime.now();
		for (int i = 0; i < 3; i++) {
			lockInfo.addFailedCount(now);
		}

		// 5분 전엔 잠금
		Assertions.assertTrue(lockInfo.isLocked(now.plusMinutes(4L)));
		// 5분 지나면 잠금 해제
		Assertions.assertFalse(lockInfo.isLocked(now.plusMinutes(5L)));

		// 한번 더 실패
		lockInfo.addFailedCount(now.plusMinutes(5L));
		// 5분 전엔 잠금
		Assertions.assertTrue(lockInfo.isLocked(now.plusMinutes(9L)));
		// 5분 지나면 잠금 해제
		Assertions.assertFalse(lockInfo.isLocked(now.plusMinutes(10L)));
	}

	@Test
	void 잠금됐을때_남은_잠금시간은_5분() {
		final LocalDateTime now = LocalDateTime.now();

		final LockInfo lockInfo = new LockInfo();
		for (int i = 0; i < 3; i++) {
			lockInfo.addFailedCount(now);
		}
		Assertions.assertEquals(Duration.ofMinutes(5L), lockInfo.getRemainLockDuration(now));
	}

	@Test
	void 잠금되고_4분_지나면_1분_남아야해() {
		final LocalDateTime now = LocalDateTime.now();

		final LockInfo lockInfo = new LockInfo();
		for (int i = 0; i < 3; i++) {
			lockInfo.addFailedCount(now);
		}
		Assertions.assertEquals(Duration.ofMinutes(1L), lockInfo.getRemainLockDuration(now.plusMinutes(4L)));
	}

	@Test
	void 시간이지나_잠금이_풀렸을때_ZERO() {
		final LocalDateTime now = LocalDateTime.now();

		final LockInfo lockInfo = new LockInfo();
		for (int i = 0; i < 3; i++) {
			lockInfo.addFailedCount(now);
		}
		Assertions.assertEquals(Duration.ZERO, lockInfo.getRemainLockDuration(now.plusMinutes(5L)));
	}

	@Test
	void 잠금된적이_없으면_ZERO() {
		final LocalDateTime now = LocalDateTime.now();
		final LockInfo lockInfo = new LockInfo();

		Assertions.assertEquals(Duration.ZERO, lockInfo.getRemainLockDuration(now));
	}

	@Test
	void 잠금_해제후엔_ZERO() {
		final LocalDateTime now = LocalDateTime.now();
		final LockInfo lockInfo = new LockInfo();
		for (int i = 0; i < 3; i++) {
			lockInfo.addFailedCount(now);
		}
		lockInfo.unlock();

		Assertions.assertEquals(Duration.ZERO, lockInfo.getRemainLockDuration(now));
	}
}
