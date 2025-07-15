package kr.co.mathrank.domain.auth.entity;

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
}