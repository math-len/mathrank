package kr.co.mathrank.domain.problem.assessment.entity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;

import org.junit.jupiter.api.Test;

class AssessmentAttemptTest {
	private static final Instant STARTED_AT = Instant.parse("2026-07-23T00:00:00Z");

	@Test
	void 답안_잠금과_시험_종료_경계를_서버시각으로_판정한다() {
		final AssessmentAttempt attempt = AssessmentAttempt.start(
			1L,
			2L,
			1,
			STARTED_AT,
			STARTED_AT.plusSeconds(900),
			STARTED_AT.plusSeconds(3600)
		);

		assertFalse(attempt.isAnswerInputEnabledAt(STARTED_AT.plusSeconds(899)));
		assertTrue(attempt.isAnswerInputEnabledAt(STARTED_AT.plusSeconds(900)));
		assertFalse(attempt.isExpiredAt(STARTED_AT.plusSeconds(3600)));
		assertTrue(attempt.isExpiredAt(STARTED_AT.plusSeconds(3601)));
	}

	@Test
	void 제출하거나_만료되면_activeKey를_비운다() {
		final AssessmentAttempt submitted = newAttempt();
		submitted.submit(10L);
		assertNull(submitted.getActiveKey());
		assertTrue(submitted.getStatus() == AssessmentAttemptStatus.SUBMITTED);

		final AssessmentAttempt expired = newAttempt();
		expired.expire();
		assertNull(expired.getActiveKey());
		assertTrue(expired.getStatus() == AssessmentAttemptStatus.EXPIRED);
	}

	private AssessmentAttempt newAttempt() {
		return AssessmentAttempt.start(
			1L,
			2L,
			1,
			STARTED_AT,
			STARTED_AT.plusSeconds(900),
			STARTED_AT.plusSeconds(3600)
		);
	}
}
