package kr.co.mathrank.domain.problem.assessment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
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
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentAttemptSubmissionCommand;
import kr.co.mathrank.domain.problem.assessment.dto.SubmissionRegisterCommand;
import kr.co.mathrank.domain.problem.assessment.entity.Assessment;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentAttempt;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentAttemptStatus;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentItem;
import kr.co.mathrank.domain.problem.assessment.exception.AssessmentAttemptException;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentAttemptRepository;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentRepository;
import kr.co.mathrank.domain.problem.core.AnswerType;
import kr.co.mathrank.domain.problem.core.Difficulty;
import kr.co.mathrank.domain.problem.core.PastProblem;

class AssessmentAttemptServiceTest {
	private static final Instant STARTED_AT = Instant.parse("2026-07-23T00:00:00Z");

	@Test
	void 시작_후_15분_전에는_제출할_수_없다() {
		final Fixture fixture = fixture(STARTED_AT.plusSeconds(899));

		assertThrows(AssessmentAttemptException.class, () -> fixture.service.submit(
			new AssessmentAttemptSubmissionCommand(2L, 1L, List.of(List.of("1")))
		));
	}

	@Test
	void 시작_후_15분부터_서버가_계산한_시간으로_제출한다() {
		final Fixture fixture = fixture(STARTED_AT.plusSeconds(900));

		assertEquals(99L, fixture.service.submit(
			new AssessmentAttemptSubmissionCommand(2L, 1L, List.of(List.of("1")))
		));
		assertEquals(AssessmentAttemptStatus.SUBMITTED, fixture.attempt.getStatus());
		assertEquals(99L, fixture.attempt.getSubmissionId());
	}

	@Test
	void 객관식은_1부터_5까지_다중선택을_허용한다() {
		final Fixture fixture = fixture(STARTED_AT.plusSeconds(900));

		fixture.service.submit(
			new AssessmentAttemptSubmissionCommand(2L, 1L, List.of(List.of("1", "3", "5")))
		);

		final ArgumentCaptor<SubmissionRegisterCommand> captor =
			ArgumentCaptor.forClass(SubmissionRegisterCommand.class);
		verify(fixture.submissionRegisterService).submitFromAttempt(captor.capture());
		assertEquals(List.of(List.of("1", "3", "5")), captor.getValue().submittedAnswers());
	}

	@Test
	void 객관식은_1부터_5_밖의_선택지를_거부한다() {
		final Fixture fixture = fixture(STARTED_AT.plusSeconds(900));

		assertThrows(AssessmentAttemptException.class, () -> fixture.service.submit(
			new AssessmentAttemptSubmissionCommand(2L, 1L, List.of(List.of("1", "6")))
		));
	}

	@Test
	void 제한시간을_넘긴_제출은_거부한다() {
		final Fixture fixture = fixture(STARTED_AT.plusSeconds(3601));

		assertThrows(AssessmentAttemptException.class, () -> fixture.service.submit(
			new AssessmentAttemptSubmissionCommand(2L, 1L, List.of(List.of("1")))
		));
	}

	@Test
	void 주관식_복수답안은_trim하고_순서를_유지해_채점에_전달한다() {
		final Fixture fixture = fixture(STARTED_AT.plusSeconds(900), AnswerType.SHORT_ANSWER);

		fixture.service.submit(
			new AssessmentAttemptSubmissionCommand(2L, 1L, List.of(List.of(" 42 ", "7")))
		);

		final ArgumentCaptor<SubmissionRegisterCommand> captor =
			ArgumentCaptor.forClass(SubmissionRegisterCommand.class);
		verify(fixture.submissionRegisterService).submitFromAttempt(captor.capture());
		assertEquals(List.of(List.of("42", "7")), captor.getValue().submittedAnswers());
	}

	@Test
	void 주관식_중복답안은_거부한다() {
		final Fixture fixture = fixture(STARTED_AT.plusSeconds(900), AnswerType.SHORT_ANSWER);

		assertThrows(AssessmentAttemptException.class, () -> fixture.service.submit(
			new AssessmentAttemptSubmissionCommand(2L, 1L, List.of(List.of("42", " 42 ")))
		));
	}

	private Fixture fixture(final Instant now) {
		return fixture(now, AnswerType.MULTIPLE_CHOICE);
	}

	private Fixture fixture(final Instant now, final AnswerType answerType) {
		final AssessmentAttemptStartManager startManager = mock(AssessmentAttemptStartManager.class);
		final AssessmentAttemptRepository attemptRepository = mock(AssessmentAttemptRepository.class);
		final AssessmentRepository assessmentRepository = mock(AssessmentRepository.class);
		final SubmissionRegisterService submissionRegisterService = mock(SubmissionRegisterService.class);
		final ProblemQueryManager problemQueryManager = mock(ProblemQueryManager.class);
		final Clock clock = Clock.fixed(now, ZoneOffset.UTC);
		final AssessmentAttempt attempt = AssessmentAttempt.start(
			1L, 2L, 1, STARTED_AT, STARTED_AT.plusSeconds(900), STARTED_AT.plusSeconds(3600));
		final Assessment assessment = Assessment.unlimited(
			1L, "test", Duration.ofMinutes(60), Duration.ofMinutes(15));
		assessment.replaceItems(List.of(AssessmentItem.of(10L, 100)));
		final ProblemQueryResult problem = new ProblemQueryResult(
			10L,
			1L,
			"problem.png",
			"path",
			Difficulty.MID,
			answerType,
			PastProblem.NONE,
			null,
			Set.of("1"),
			null,
			null,
			null,
			null,
			null,
			null
		);

		when(attemptRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(attempt));
		when(assessmentRepository.findWithItems(1L)).thenReturn(Optional.of(assessment));
		when(problemQueryManager.getProblemInfo(10L)).thenReturn(problem);
		when(submissionRegisterService.submitFromAttempt(any())).thenReturn(99L);

		return new Fixture(
			new AssessmentAttemptService(
				startManager,
				attemptRepository,
				assessmentRepository,
				submissionRegisterService,
				problemQueryManager,
				clock
			),
			attempt,
			submissionRegisterService
		);
	}

	private record Fixture(
		AssessmentAttemptService service,
		AssessmentAttempt attempt,
		SubmissionRegisterService submissionRegisterService
	) {
	}
}
