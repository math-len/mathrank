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
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.use_sql_comments=true
spring.jpa.properties.hibernate.format_sql=true
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
	void 채점_이후엔_FINISHED_상태로_전환된다() {
		final Assessment assessment = Assessment.of(1L, "test", Duration.ofMinutes(10));
		assessment.replaceItems(List.of(AssessmentItem.of(1L, 10), AssessmentItem.of(2L, 90))); // 문항 2개

		final List<List<String>> answersWithWrongCount = List.of(List.of("1"), List.of("2")); // 답안 2개
		assessment.registerSubmission(1L, answersWithWrongCount, Duration.ofMinutes(5), true);

		assessmentRepository.save(assessment);
		final Long submissionId = assessment.getAssessmentSubmissions().getFirst().getId();

		Mockito.when(problemClient.matchAnswer(Mockito.anyLong(), Mockito.anyList()))
			.thenReturn(new SolveResult(true, Collections.emptySet(), Collections.emptyList()));

		// 채점하기
		submissionGradeService.evaluateSubmission(submissionId);

		// 결과 확인하기
		Assertions.assertEquals(EvaluationStatus.FINISHED, assessmentSubmissionRepository.findById(submissionId).get().getEvaluationStatus());
	}

	@Test
	void 채점결과_합산_테스트() {
		final Assessment assessment = Assessment.of(1L, "test", Duration.ofMinutes(10));
		assessment.replaceItems(List.of(AssessmentItem.of(1L, 10), AssessmentItem.of(2L, 90))); // 문항 2개

		final List<List<String>> answersWithWrongCount = List.of(List.of("1"), List.of("2")); // 답안 2개
		assessment.registerSubmission(1L, answersWithWrongCount, Duration.ofMinutes(5), true);

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
		assessment.registerSubmission(1L, answersWithWrongCount, Duration.ofMinutes(5), true);

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

	@Test
	void 사용자의_첫_제출_점수만_assessment_평균점수에_반영된다() {
		final Assessment assessment = Assessment.of(1L, "test", Duration.ofMinutes(10));
		final Long problemId1 = 1L;
		final Long problemId2 = 2L;
		assessment.replaceItems(List.of(
			AssessmentItem.of(problemId1, 10), // 10 점 문제
			AssessmentItem.of(problemId2, 90)) // 90 점 문제
		);

		final List<List<String>> submittedAnswers = List.of(List.of("1"), List.of("2"));
		// 첫 제출
		assessment.registerSubmission(1L, submittedAnswers, Duration.ofMinutes(5), true);
		// 같은 사용자의 두번째 제출
		assessment.registerSubmission(1L, submittedAnswers, Duration.ofMinutes(5), false);
		assessmentRepository.save(assessment);


		final Long submissionId = assessment.getAssessmentSubmissions().getFirst().getId();

		// 채점 시, 항상 정답으로 응답
		Mockito.when(problemClient.matchAnswer(Mockito.anyLong(), Mockito.anyList()))
			.thenReturn(new SolveResult(true, Collections.emptySet(), Collections.emptyList()));

		// 채점하기
		submissionGradeService.evaluateSubmission(submissionId);

		// 첫번째 제출 채점점수 100점
		// 두번째 제출 채점점수 100점

		// 첫번째 제출점수만 반영되야 함으로,
		// 따라서 총 점수는 100점
		final Assessment targetAssessment = assessmentRepository.findById(assessment.getId())
			.orElseThrow();
		Assertions.assertEquals(100, targetAssessment.getTotalScore());
		// 한명이 두번 제출했음으로 distinctCount = 1
		Assertions.assertEquals(1, targetAssessment.getDistinctTriedMemberCount());
	}

	@Test
	void 여러_사용자의_첫_제출_점수들은_모두_assessment_평균점수에_반영된다() {
		final Assessment assessment = Assessment.of(1L, "test", Duration.ofMinutes(10));
		final Long problemId1 = 1L;
		final Long problemId2 = 2L;
		assessment.replaceItems(List.of(
			AssessmentItem.of(problemId1, 10), // 10 점 문제
			AssessmentItem.of(problemId2, 90)) // 90 점 문제
		);

		final List<List<String>> submittedAnswers = List.of(List.of("1"), List.of("2"));
		// 첫 제출
		assessment.registerSubmission(1L, submittedAnswers, Duration.ofMinutes(5), true);
		// 다른 사용자의 첫 제출
		assessment.registerSubmission(2L, submittedAnswers, Duration.ofMinutes(5), true);
		// 다른 사용자의 첫 제출
		assessment.registerSubmission(3L, submittedAnswers, Duration.ofMinutes(5), true);
		assessmentRepository.save(assessment);


		final Long submissionId1 = assessment.getAssessmentSubmissions().get(0).getId();
		final Long submissionId2 = assessment.getAssessmentSubmissions().get(1).getId();
		final Long submissionId3 = assessment.getAssessmentSubmissions().get(2).getId();

		// 채점 시, 항상 정답으로 응답
		Mockito.when(problemClient.matchAnswer(Mockito.anyLong(), Mockito.anyList()))
			.thenReturn(new SolveResult(true, Collections.emptySet(), Collections.emptyList()));

		// 모두 채점하기
		submissionGradeService.evaluateSubmission(submissionId1);
		submissionGradeService.evaluateSubmission(submissionId2);
		submissionGradeService.evaluateSubmission(submissionId3);

		// 첫번째 제출 채점점수 100점
		// 두번째 제출 채점점수 100점
		// 세번째 제출 채점점수 100점

		// 첫번째 제출점수만 반영되야 함으로,
		// 따라서 총 점수는 300점
		final Assessment targetAssessment = assessmentRepository.findById(assessment.getId())
			.orElseThrow();
		Assertions.assertEquals(300, targetAssessment.getTotalScore());
		Assertions.assertEquals(3, targetAssessment.getDistinctTriedMemberCount());
	}

	@AfterEach
	void clear() {
		assessmentRepository.deleteAll();
	}
}
