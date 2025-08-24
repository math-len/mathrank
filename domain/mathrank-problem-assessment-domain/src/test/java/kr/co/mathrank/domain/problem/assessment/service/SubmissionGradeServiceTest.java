package kr.co.mathrank.domain.problem.assessment.service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import kr.co.mathrank.client.internal.problem.ProblemClient;
import kr.co.mathrank.client.internal.problem.SolveResult;
import kr.co.mathrank.domain.problem.assessment.entity.Assessment;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentItem;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentSubmission;
import kr.co.mathrank.domain.problem.assessment.entity.EvaluationStatus;
import kr.co.mathrank.domain.problem.assessment.exception.NoSuchSubmissionException;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentRepository;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentSubmissionRepository;

@SpringBootTest(properties = """
client.problem.host=tset
client.problem.port=10202
""")
class SubmissionGradeServiceTest {
	@MockitoBean
	private ProblemClient problemClient;

	@Autowired
	private SubmissionGradeService submissionGradeService;
	@Autowired
	private AssessmentRepository assessmentRepository;
	@Autowired
	private AssessmentSubmissionRepository assessmentSubmissionRepository;

	@Test
	void 존재하지_않는_답안지_채점_시도시_에러() {
		Assertions.assertThrows(NoSuchSubmissionException.class,
			() -> submissionGradeService.evaluateSubmission(2139123812L));
	}

	@Test
	void 채점결과_합산_테스트() {
		final Assessment assessment = Assessment.of(1L, "test", Duration.ofMinutes(10));
		assessment.replaceItems(List.of(AssessmentItem.of(1L, 10), AssessmentItem.of(2L, 90))); // 문항 2개

		final List<List<String>> answersWithWrongCount = List.of(List.of("1"), List.of("2")); // 답안 2개
		assessment.registerSubmission(1L, answersWithWrongCount, Duration.ofMinutes(5));

		assessmentRepository.save(assessment);
		final Long submissionId = assessment.getAssessmentSubmissions().getFirst().getId();

		Mockito.when(problemClient.matchAnswer(Mockito.anyLong(), Mockito.anyList()))
			.thenReturn(new SolveResult(true, Collections.emptySet(), Collections.emptyList()));

		// 채점하기
		submissionGradeService.evaluateSubmission(submissionId);

		// 결과 확인하기
		final int score = assessmentSubmissionRepository.findById(submissionId).orElseThrow()
				.getTotalScore();

		Assertions.assertEquals(100, score);
	}

	@Test
	void 모든_문제가_채점되면_해당_답안지는_채점완료_처리된다() {
		final Assessment assessment = Assessment.of(1L, "test", Duration.ofMinutes(10));
		assessment.replaceItems(List.of(AssessmentItem.of(1L, 10), AssessmentItem.of(2L, 90))); // 문항 2개

		final List<List<String>> answersWithWrongCount = List.of(List.of("1"), List.of("2")); // 답안 2개
		assessment.registerSubmission(1L, answersWithWrongCount, Duration.ofMinutes(5));

		assessmentRepository.save(assessment);
		final Long submissionId = assessment.getAssessmentSubmissions().getFirst().getId();

		Mockito.when(problemClient.matchAnswer(Mockito.anyLong(), Mockito.anyList()))
			.thenReturn(new SolveResult(true, Collections.emptySet(), Collections.emptyList()));

		// 채점하기
		submissionGradeService.evaluateSubmission(submissionId);

		// 결과 확인하기
		final AssessmentSubmission submission = assessmentSubmissionRepository.findById(submissionId).orElseThrow();


		Assertions.assertEquals(EvaluationStatus.FINISHED, submission.getEvaluationStatus());
	}

	@AfterEach
	void clear() {
		assessmentRepository.deleteAll();
	}
}
