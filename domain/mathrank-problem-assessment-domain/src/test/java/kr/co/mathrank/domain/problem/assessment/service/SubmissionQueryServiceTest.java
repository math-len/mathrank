package kr.co.mathrank.domain.problem.assessment.service;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.co.mathrank.domain.problem.assessment.entity.Assessment;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentItem;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentSubmission;
import kr.co.mathrank.domain.problem.assessment.entity.EvaluationStatus;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentRepository;

@SpringBootTest(properties =
"""
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
""")
class SubmissionQueryServiceTest {
	@Autowired
	private SubmissionQueryService submissionQueryService;
	@Autowired
	private AssessmentRepository assessmentRepository;

	@Test
	@Transactional
	void 등록후에는_PENDING_상태로_조회된다() {
		final Assessment assessment = Assessment.of(1L, "test", Duration.ofMinutes(100L));
		// 시험 문제 번호
		assessment.replaceItems(
			List.of(
				AssessmentItem.of(1L, 25),
				AssessmentItem.of(1L, 25),
				AssessmentItem.of(1L, 25),
				AssessmentItem.of(1L, 25))
		);

		final List<String> submittedAnswer = List.of("1", "2", "3", "4");
		final AssessmentSubmission submission = assessment.registerSubmission(
			1L, List.of(submittedAnswer, submittedAnswer, submittedAnswer, submittedAnswer), Duration.ofMinutes(5));
		assessmentRepository.save(assessment);

		// 쿼리 한번으로 수행되는지도 확인
		Assertions.assertEquals(
			EvaluationStatus.PENDING,
			submissionQueryService.getSubmissionResult(submission.getId()).evaluationStatus()
		);
	}
}
