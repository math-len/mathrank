package kr.co.mathrank.domain.problem.assessment.service;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.co.mathrank.domain.problem.assessment.dto.AssessmentSolutionQuery;
import kr.co.mathrank.domain.problem.assessment.entity.Assessment;
import kr.co.mathrank.domain.problem.assessment.exception.CannotGetSolutionException;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentRepository;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentSubmissionRepository;

@SpringBootTest
@Transactional
class AssessmentSolutionQueryServiceTest {
	@Autowired
	private AssessmentSolutionQueryService solutionQueryService;
	@Autowired
	private AssessmentRepository assessmentRepository;
	@Autowired
	private AssessmentSubmissionRepository assessmentSubmissionRepository;

	@Test
	void 풀지_않은_시험지의_정답_조회시_예외발생() {
		final Long requestMemberId = 1L;

		final Assessment assessment = Assessment.unlimited(1L, "testAssessment", Duration.ofMinutes(100L));
		assessment.registerSubmission(requestMemberId, List.of(), Duration.ofMinutes(1L), true);
		assessmentRepository.save(assessment);

		Assertions.assertThrows(CannotGetSolutionException.class, () -> solutionQueryService.querySolutions(
			new AssessmentSolutionQuery(assessment.getId(), requestMemberId + 1)));
	}

	@Test
	void 푼_시험지_정답_조회시_정상_응답() {
		final Long requestMemberId = 1L;

		final Assessment assessment = Assessment.unlimited(1L, "testAssessment", Duration.ofMinutes(100L));
		assessment.registerSubmission(requestMemberId, List.of(), Duration.ofMinutes(1L), true);
		assessmentRepository.save(assessment);

		Assertions.assertDoesNotThrow(() -> solutionQueryService.querySolutions(
			new AssessmentSolutionQuery(assessment.getId(), requestMemberId)));
	}
}