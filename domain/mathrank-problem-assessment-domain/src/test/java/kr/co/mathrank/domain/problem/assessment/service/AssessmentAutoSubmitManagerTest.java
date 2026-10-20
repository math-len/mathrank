package kr.co.mathrank.domain.problem.assessment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import kr.co.mathrank.client.internal.problem.ProblemQueryResult;
import kr.co.mathrank.domain.problem.assessment.dto.SubmissionRegisterCommand;
import kr.co.mathrank.domain.problem.assessment.entity.Assessment;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentAttempt;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentAttemptStatus;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentItem;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentAttemptRepository;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentRepository;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentSubmissionRepository;
import kr.co.mathrank.domain.problem.core.AnswerType;
import kr.co.mathrank.domain.problem.core.Difficulty;
import kr.co.mathrank.domain.problem.core.PastProblem;

class AssessmentAutoSubmitManagerTest {
	private static final Instant STARTED_AT = Instant.parse("2026-10-20T00:00:00Z");

	@Test
	void 시작_3시간이_지나면_임시저장_답안을_정리해_3시간_풀이로_제출한다() {
		final Fixture fixture = fixture(STARTED_AT.plus(Duration.ofHours(3)));
		fixture.attempt.saveDraft(List.of(List.of("5", " 2 ", "9", "2"), List.of(" 42 ")), STARTED_AT);

		assertTrue(fixture.manager.autoSubmitIfDue(1L));

		final ArgumentCaptor<SubmissionRegisterCommand> captor = ArgumentCaptor.forClass(SubmissionRegisterCommand.class);
		verify(fixture.submissionRegisterService).submitFromAttempt(captor.capture());
		assertEquals(List.of(List.of("2", "5"), List.of("42")), captor.getValue().submittedAnswers());
		assertEquals(Duration.ofHours(3), captor.getValue().elapsedTime());
		assertEquals(AssessmentAttemptStatus.SUBMITTED, fixture.attempt.getStatus());
		assertEquals(99L, fixture.attempt.getSubmissionId());
	}

	@Test
	void 임시저장_답안이_없으면_빈_답안으로_제출한다() {
		final Fixture fixture = fixture(STARTED_AT.plus(Duration.ofHours(4)));

		assertTrue(fixture.manager.autoSubmitIfDue(1L));

		final ArgumentCaptor<SubmissionRegisterCommand> captor = ArgumentCaptor.forClass(SubmissionRegisterCommand.class);
		verify(fixture.submissionRegisterService).submitFromAttempt(captor.capture());
		assertEquals(List.of(List.of(), List.of()), captor.getValue().submittedAnswers());
	}

	@Test
	void 마감_전이거나_이미_제출된_응시는_건드리지_않는다() {
		final Fixture early = fixture(STARTED_AT.plus(Duration.ofHours(3)).minusSeconds(1));
		assertFalse(early.manager.autoSubmitIfDue(1L));

		final Fixture submitted = fixture(STARTED_AT.plus(Duration.ofHours(4)));
		submitted.attempt.submit(7L);
		assertFalse(submitted.manager.autoSubmitIfDue(1L));

		verify(early.submissionRegisterService, never()).submitFromAttempt(any());
		verify(submitted.submissionRegisterService, never()).submitFromAttempt(any());
	}

	@Test
	void 이미_제출_기록이_있는_회원의_응시는_제출하지_않고_만료한다() {
		final Fixture fixture = fixture(STARTED_AT.plus(Duration.ofHours(4)));
		when(fixture.submissionRepository.existsByAssessmentIdAndMemberId(10L, 2L)).thenReturn(true);

		assertFalse(fixture.manager.autoSubmitIfDue(1L));

		assertEquals(AssessmentAttemptStatus.EXPIRED, fixture.attempt.getStatus());
		verify(fixture.submissionRegisterService, never()).submitFromAttempt(any());
	}

	private Fixture fixture(final Instant now) {
		final AssessmentAttemptRepository attemptRepository = mock(AssessmentAttemptRepository.class);
		final AssessmentRepository assessmentRepository = mock(AssessmentRepository.class);
		final AssessmentSubmissionRepository submissionRepository = mock(AssessmentSubmissionRepository.class);
		final SubmissionRegisterService submissionRegisterService = mock(SubmissionRegisterService.class);
		final ProblemQueryManager problemQueryManager = mock(ProblemQueryManager.class);
		final AssessmentAttempt attempt = AssessmentAttempt.start(10L, 2L, 1, STARTED_AT, STARTED_AT, null);
		final Assessment assessment = Assessment.unlimited(
			1L, "시험지", Duration.ofMinutes(60), Duration.ofMinutes(0));
		assessment.replaceItems(List.of(AssessmentItem.of(100L, 50), AssessmentItem.of(200L, 50)));

		when(attemptRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(attempt));
		when(assessmentRepository.findWithItems(10L)).thenReturn(Optional.of(assessment));
		when(problemQueryManager.getProblemInfo(100L)).thenReturn(problem(100L, AnswerType.MULTIPLE_CHOICE));
		when(problemQueryManager.getProblemInfo(200L)).thenReturn(problem(200L, AnswerType.SHORT_ANSWER));
		when(submissionRegisterService.submitFromAttempt(any())).thenReturn(99L);

		final AssessmentAutoSubmitManager manager = new AssessmentAutoSubmitManager(
			attemptRepository,
			assessmentRepository,
			submissionRepository,
			submissionRegisterService,
			problemQueryManager,
			Clock.fixed(now, ZoneOffset.UTC)
		);
		return new Fixture(manager, attempt, submissionRegisterService, submissionRepository);
	}

	private ProblemQueryResult problem(final Long problemId, final AnswerType type) {
		return new ProblemQueryResult(
			problemId, 1L, "problem.png", "path", Difficulty.MID, type, PastProblem.NONE,
			null, Set.of("1"), null, null, null, null, null, null);
	}

	private record Fixture(
		AssessmentAutoSubmitManager manager,
		AssessmentAttempt attempt,
		SubmissionRegisterService submissionRegisterService,
		AssessmentSubmissionRepository submissionRepository
	) {
	}
}
