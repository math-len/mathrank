package kr.co.mathrank.domain.problem.assessment.service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import kr.co.mathrank.client.internal.problem.ProblemClient;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentSubmissionStatisticQueryResult;
import kr.co.mathrank.domain.problem.assessment.entity.Assessment;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentItem;
import kr.co.mathrank.domain.problem.assessment.entity.GradeResult;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentRepository;

@SpringBootTest
class AssessmentStatisticsServiceTest {
	@MockitoBean
	private ProblemClient problemClient;

	@Autowired
	private AssessmentStatisticsService assessmentStatisticsService;
	@Autowired
	private AssessmentRepository assessmentRepository;

	@Test
	void 평가되지않은_시험지는_통계에_포함되지_않는다() {
		final Assessment assessment = Assessment.unlimited(1L, "test", Duration.ofMinutes(10));

		assessment.replaceItems(List.of(AssessmentItem.of(1L, 10))); // 문항 2개

		final List<List<String>> answersWithWrongCount = List.of(List.of("1")); // 답안 2개
		assessment.registerSubmission(1L, answersWithWrongCount, Duration.ofMinutes(1), true);
		assessment.registerSubmission(1L, answersWithWrongCount, Duration.ofMinutes(9), false);

		assessmentRepository.save(assessment);

		final AssessmentSubmissionStatisticQueryResult result = assessmentStatisticsService.query(assessment.getId());

		Assertions.assertTrue(result.ascendingElapsedTimes().isEmpty());
		Assertions.assertTrue(result.descendingScores().isEmpty());
	}

	@Test
	void 첫번째_제출만_통계에_포함된다() {
		final Assessment assessment = Assessment.unlimited(1L, "test", Duration.ofMinutes(10));

		// 첫번째 사용자가 걸린 시간
		final Duration firstUsersElapsedTime = Duration.ofMinutes(5);
		assessment.replaceItems(List.of(AssessmentItem.of(1L, 10)));

		final List<List<String>> answers = List.of(List.of("1"));
		assessment.registerSubmission(1L, answers, firstUsersElapsedTime, true);
		assessment.registerSubmission(1L, answers, Duration.ofMinutes(9), false);

		assessment.getAssessmentSubmissions()
			.forEach(assessmentSubmission -> {
				assessmentSubmission.grade(List.of(new GradeResult(1L, Collections.emptyList(), true)));
			});

		assessmentRepository.save(assessment);

		final AssessmentSubmissionStatisticQueryResult result = assessmentStatisticsService.query(assessment.getId());

		// 통계조회할땐 첫번째 시도만 찾음
		Assertions.assertAll(
			() -> Assertions.assertEquals(firstUsersElapsedTime, result.ascendingElapsedTimes().getFirst()),
			() -> Assertions.assertEquals(1, result.descendingScores().size()),
			() -> Assertions.assertEquals(1, result.ascendingElapsedTimes().size())
		);
	}


	@Test
	void 각_사용자의_첫번째_제출만_통계에_포함된다() {
		final Assessment assessment = Assessment.unlimited(1L, "test", Duration.ofMinutes(10));

		// 첫번째 사용자가 걸린 시간
		final Duration firstUsersElapsedTime = Duration.ofMinutes(5);
		final Duration secondUsersElapsedTime = Duration.ofMinutes(6);
		assessment.replaceItems(List.of(AssessmentItem.of(1L, 10)));

		final List<List<String>> answers = List.of(List.of("1"));
		assessment.registerSubmission(1L, answers, firstUsersElapsedTime, true);
		assessment.registerSubmission(1L, answers, Duration.ofMinutes(4), false);
		assessment.registerSubmission(2L, answers, secondUsersElapsedTime, true);
		assessment.registerSubmission(2L, answers, Duration.ofMinutes(6), false);

		assessment.getAssessmentSubmissions()
			.forEach(assessmentSubmission -> {
				assessmentSubmission.grade(List.of(new GradeResult(1L, Collections.emptyList(), true)));
			});

		assessmentRepository.save(assessment);

		final AssessmentSubmissionStatisticQueryResult result = assessmentStatisticsService.query(assessment.getId());

		// 통계조회할땐 첫번째 시도만 찾음
		Assertions.assertAll(
			() -> Assertions.assertEquals(firstUsersElapsedTime, result.ascendingElapsedTimes().getFirst()),
			() -> Assertions.assertEquals(secondUsersElapsedTime, result.ascendingElapsedTimes().get(1)),
			() -> Assertions.assertEquals(2, result.descendingScores().size()),
			() -> Assertions.assertEquals(2, result.ascendingElapsedTimes().size())
		);
	}
}
