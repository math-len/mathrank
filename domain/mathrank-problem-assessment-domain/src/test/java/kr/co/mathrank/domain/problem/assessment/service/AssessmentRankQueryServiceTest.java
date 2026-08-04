package kr.co.mathrank.domain.problem.assessment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.co.mathrank.domain.problem.assessment.dto.AssessmentSubmissionRankResult;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentSubmissionStanding;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentSubmissionStatisticQueryResult;
import kr.co.mathrank.domain.problem.assessment.entity.Assessment;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentItem;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentSubmission;
import kr.co.mathrank.domain.problem.assessment.entity.GradeResult;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentSubmissionRepository;

@ExtendWith(MockitoExtension.class)
class AssessmentRankQueryServiceTest {
	@Mock
	private AssessmentSubmissionRepository assessmentSubmissionRepository;
	@Mock
	private AssessmentStatisticsService assessmentStatisticsService;

	private AssessmentRankQueryService service;

	@BeforeEach
	void setUp() {
		service = new AssessmentRankQueryService(assessmentSubmissionRepository, assessmentStatisticsService);
	}

	@Test
	void 점수_우선_동점이면_풀이시간으로_공식등수를_계산한다() {
		final AssessmentSubmission submission = finishedSubmission(true, Duration.ofMinutes(7));
		final List<AssessmentSubmissionStanding> standings = List.of(
			new AssessmentSubmissionStanding(100, Duration.ofMinutes(5)),
			new AssessmentSubmissionStanding(100, Duration.ofMinutes(7)),
			new AssessmentSubmissionStanding(90, Duration.ofMinutes(1))
		);
		mockResult(submission, standings);

		final AssessmentSubmissionRankResult result = service.getRank(1L);

		assertEquals(2, result.overallRank());
		assertEquals(3, result.totalUserCount());
		assertTrue(result.rankEligible());
	}

	@Test
	void 점수와_시간이_같으면_공동등수다() {
		final AssessmentSubmission submission = finishedSubmission(true, Duration.ofMinutes(5));
		final List<AssessmentSubmissionStanding> standings = List.of(
			new AssessmentSubmissionStanding(100, Duration.ofMinutes(5)),
			new AssessmentSubmissionStanding(100, Duration.ofMinutes(5)),
			new AssessmentSubmissionStanding(90, Duration.ofMinutes(1))
		);
		mockResult(submission, standings);

		assertEquals(1, service.getRank(1L).overallRank());
	}

	@Test
	void 재응시는_공식등수에_포함하지_않는다() {
		final AssessmentSubmission submission = finishedSubmission(false, Duration.ofMinutes(3));
		mockResult(submission, List.of(
			new AssessmentSubmissionStanding(100, Duration.ofMinutes(5))
		));

		final AssessmentSubmissionRankResult result = service.getRank(1L);

		assertFalse(result.rankEligible());
		assertNull(result.overallRank());
		assertEquals(1, result.totalUserCount());
	}

	private AssessmentSubmission finishedSubmission(final boolean first, final Duration elapsedTime) {
		final Assessment assessment = Assessment.unlimited(1L, "test", Duration.ofMinutes(60));
		assessment.replaceItems(List.of(AssessmentItem.of(10L, 100)));
		final AssessmentSubmission submission = assessment.registerSubmission(
			2L, List.of(List.of("1")), elapsedTime, first);
		submission.grade(List.of(new GradeResult(10L, Collections.singletonList("1"), true)));
		return submission;
	}

	private void mockResult(
		final AssessmentSubmission submission,
		final List<AssessmentSubmissionStanding> standings
	) {
		when(assessmentSubmissionRepository.findById(1L)).thenReturn(Optional.of(submission));
		when(assessmentStatisticsService.query(submission.getAssessment().getId())).thenReturn(
			new AssessmentSubmissionStatisticQueryResult(
				submission.getAssessment().getId(),
				standings.stream().map(AssessmentSubmissionStanding::totalScore).toList(),
				standings.stream().map(AssessmentSubmissionStanding::elapsedTime).sorted().toList(),
				standings
			)
		);
	}
}
