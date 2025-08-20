package kr.co.mathrank.domain.problem.assessment.entity;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentRepository;

@DataJpaTest(showSql = true)
class AssessmentItemSubmissionTest {
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	private AssessmentRepository assessmentRepository;

	@Test
	void 시험지에_제출된_정답은_DB에_json_형식으로_저장된다() {
		// given
		final Assessment assessment = Assessment.of(1L, "test", Duration.ofMinutes(10));
		// 한 문제 등록
		assessment.replaceItems(List.of(AssessmentItem.of(1L, 10)));
		// 문항에 대한 답안 내용 (정답이 여러 개일 수 있음을 가정)
		final List<String> submittedAnswer = List.of("1", "2", "3");
		assessment.registerSubmission(1L, List.of(submittedAnswer));

		// when
		final Long assessmentId = assessmentRepository.save(assessment).getId();
		entityManager.flush();
		entityManager.clear();

		// then
		final Assessment savedAssessment = assessmentRepository.findById(assessmentId)
			.orElseThrow();
		final AssessmentSubmission submission = savedAssessment.getAssessmentSubmissions().getFirst();
		final AssessmentItemSubmission itemSubmission = submission.getSubmittedItemAnswers().getFirst();

		// DB에 저장된 JSON 값이 올바르게 역직렬화되는지 확인
		Assertions.assertIterableEquals(submittedAnswer, itemSubmission.getSubmittedAnswer());
	}
}
