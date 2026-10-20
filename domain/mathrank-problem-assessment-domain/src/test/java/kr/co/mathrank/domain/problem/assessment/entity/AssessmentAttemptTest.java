package kr.co.mathrank.domain.problem.assessment.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

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

	@Test
	void 무제한_응시는_만료되지_않고_즉시_답안을_입력할_수_있다() {
		final AssessmentAttempt attempt = AssessmentAttempt.start(
			1L,
			2L,
			1,
			STARTED_AT,
			STARTED_AT,
			null
		);

		assertFalse(attempt.isExpiredAt(STARTED_AT.plusSeconds(86400)));
		assertTrue(attempt.isAnswerInputEnabledAt(STARTED_AT));
	}

	@Test
	void 기존_활성회차의_잠금과_만료도_제거할_수_있다() {
		final AssessmentAttempt attempt = newAttempt();

		attempt.removeTimeLimits();

		assertTrue(attempt.isAnswerInputEnabledAt(STARTED_AT));
		assertNull(attempt.getExpiresAt());
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

	@Test
	void 제한시간_없는_응시는_시작_3시간_뒤부터_자동제출_대상이다() {
		final AssessmentAttempt untimed = AssessmentAttempt.start(1L, 2L, 1, STARTED_AT, STARTED_AT, null);

		assertFalse(untimed.isAutoSubmitDueAt(STARTED_AT.plus(Duration.ofHours(3)).minusSeconds(1)));
		assertTrue(untimed.isAutoSubmitDueAt(STARTED_AT.plus(Duration.ofHours(3))));

		untimed.submit(10L);
		assertFalse(untimed.isAutoSubmitDueAt(STARTED_AT.plus(Duration.ofHours(4))));
	}

	@Test
	void 제한시간_있는_응시는_자동제출하지_않는다() {
		final AssessmentAttempt timed = AssessmentAttempt.start(
			1L, 2L, 1, STARTED_AT, STARTED_AT, STARTED_AT.plus(Duration.ofHours(5)));

		assertNull(timed.getAutoSubmitAt());
		assertFalse(timed.isAutoSubmitDueAt(STARTED_AT.plus(Duration.ofHours(4))));
	}

	@Test
	void 자동제출_답안은_문항_수에_맞춰_빈_답으로_채운다() {
		final AssessmentAttempt attempt = AssessmentAttempt.start(1L, 2L, 1, STARTED_AT, STARTED_AT, null);
		assertEquals(List.of(List.of(), List.of()), attempt.draftAnswersFor(2));

		attempt.saveDraft(List.of(List.of("3")), STARTED_AT);
		assertEquals(List.of(List.of("3"), List.of()), attempt.draftAnswersFor(2));
		assertEquals(List.of(List.of("3")), attempt.draftAnswersFor(1));
	}
}
