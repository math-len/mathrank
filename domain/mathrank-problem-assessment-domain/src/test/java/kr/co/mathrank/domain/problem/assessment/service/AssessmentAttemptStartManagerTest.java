package kr.co.mathrank.domain.problem.assessment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import kr.co.mathrank.domain.problem.assessment.entity.Assessment;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentAttempt;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentAttemptRepository;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentRepository;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentSubmissionRepository;

class AssessmentAttemptStartManagerTest {
	@Test
	void 무제한_시험지는_서버_시작시각부터_즉시_입력하고_만료시각이_없다() {
		final Instant now = Instant.parse("2026-09-29T00:00:00Z");
		final AssessmentRepository assessmentRepository = mock(AssessmentRepository.class);
		final AssessmentAttemptRepository attemptRepository = mock(AssessmentAttemptRepository.class);
		final AssessmentSubmissionRepository submissionRepository = mock(AssessmentSubmissionRepository.class);
		final Assessment assessment = Assessment.unlimited(
			1L, "무제한 시험지", Duration.ofMinutes(60), Duration.ofMinutes(15));
		when(assessmentRepository.findById(10L)).thenReturn(Optional.of(assessment));
		when(attemptRepository.findByActiveKeyForUpdate(any())).thenReturn(Optional.empty());
		when(attemptRepository.findTopByAssessmentIdAndMemberIdOrderByAttemptNumberDesc(10L, 20L))
			.thenReturn(Optional.empty());
		when(attemptRepository.saveAndFlush(any(AssessmentAttempt.class)))
			.thenAnswer(invocation -> invocation.getArgument(0));
		when(submissionRepository.existsByAssessmentIdAndMemberId(10L, 20L)).thenReturn(false);
		final AssessmentAttemptStartManager manager = new AssessmentAttemptStartManager(
			assessmentRepository,
			attemptRepository,
			submissionRepository,
			Clock.fixed(now, ZoneOffset.UTC)
		);

		final var result = manager.start(10L, 20L);

		assertEquals(now, result.startedAt());
		assertEquals(now, result.answerUnlockedAt());
		assertNull(result.expiresAt());
	}
}
