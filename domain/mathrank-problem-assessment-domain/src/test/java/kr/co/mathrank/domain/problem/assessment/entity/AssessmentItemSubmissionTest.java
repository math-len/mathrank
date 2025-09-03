package kr.co.mathrank.domain.problem.assessment.entity;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kr.co.mathrank.common.querydsl.MathrankQueryDslConfiguration;
import kr.co.mathrank.domain.problem.assessment.exception.AssessmentSubmissionRegisterException;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentRepository;

@DataJpaTest(showSql = true)
@Import(MathrankQueryDslConfiguration.class)
class AssessmentItemSubmissionTest {
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	private AssessmentRepository assessmentRepository;

	@Test
	void 채점결과의_갯수가_다르면_예외발생() {
		final Assessment assessment = Assessment.unlimited(1L, "test", Duration.ofMinutes(10));
		// 한 문제 등록
		assessment.replaceItems(List.of(AssessmentItem.of(1L, 10)));
		// 문항에 대한 답안 내용 (정답이 여러 개일 수 있음을 가정)
		final List<String> submittedAnswer = List.of("1", "2", "3");
		final AssessmentSubmission submission = assessment.registerSubmission(1L, List.of(submittedAnswer), Duration.ofMinutes(5), true);

		// 채점 결과 두개 넣기
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			submission.grade(List.of(new GradeResult(1L, List.of("1"), true), new GradeResult(1L, List.of("1"), true)));
		});
	}

	@Test
	void 문제의_problemId와_채점결과의_problemId가_다르면_예외발생() {
		final Assessment assessment = Assessment.unlimited(1L, "test", Duration.ofMinutes(10));
		// 한 문제 등록
		assessment.replaceItems(List.of(AssessmentItem.of(1L, 10)));
		// 문항에 대한 답안 내용 (정답이 여러 개일 수 있음을 가정)
		final List<String> submittedAnswer = List.of("1", "2", "3");
		final AssessmentSubmission submission = assessment.registerSubmission(1L, List.of(submittedAnswer), Duration.ofMinutes(5), true);

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			submission.grade(List.of(new GradeResult(110L, List.of("1"), true)));
		});
	}

	@Test
	void 채점을_확인하여_점수를_반환한다() {
		final Assessment assessment = Assessment.unlimited(1L, "test", Duration.ofMinutes(10));
		// 한 문제 등록
		assessment.replaceItems(List.of(AssessmentItem.of(1L, 10)));
		// 문항에 대한 답안 내용 (정답이 여러 개일 수 있음을 가정)
		final List<String> submittedAnswer = List.of("1", "2", "3");
		final AssessmentSubmission submission = assessment.registerSubmission(1L, List.of(submittedAnswer), Duration.ofMinutes(5), true);


		submission.grade(List.of(new GradeResult(1L, List.of("1"), true)));
		Assertions.assertEquals(10, submission.getTotalScore());
	}

	@Test
	void 채점이_완료되면_체점상태_변경() {
		final Assessment assessment = Assessment.unlimited(1L, "test", Duration.ofMinutes(10));
		// 한 문제 등록
		assessment.replaceItems(List.of(AssessmentItem.of(1L, 10)));
		// 문항에 대한 답안 내용 (정답이 여러 개일 수 있음을 가정)
		final List<String> submittedAnswer = List.of("1", "2", "3");
		final AssessmentSubmission submission = assessment.registerSubmission(1L, List.of(submittedAnswer), Duration.ofMinutes(5), true);


		submission.grade(List.of(new GradeResult(1L, List.of("1"), true)));
		Assertions.assertEquals(EvaluationStatus.FINISHED, submission.getEvaluationStatus());
	}

	@Test
	void 채점은_다시_시도될_수_없다() {
		final Assessment assessment = Assessment.unlimited(1L, "test", Duration.ofMinutes(10));
		// 한 문제 등록
		assessment.replaceItems(List.of(AssessmentItem.of(1L, 10)));
		// 문항에 대한 답안 내용 (정답이 여러 개일 수 있음을 가정)
		final List<String> submittedAnswer = List.of("1", "2", "3");
		final AssessmentSubmission submission = assessment.registerSubmission(1L, List.of(submittedAnswer), Duration.ofMinutes(5), true);


		submission.grade(List.of(new GradeResult(1L, List.of("1"), true)));

		Assertions.assertThrows(IllegalStateException.class, () -> {
			submission.grade(List.of(new GradeResult(1L, List.of("1"), true)));
		});
	}

	@Test
	void 시험지에_제출된_정답은_DB에_json_형식으로_저장된다() {
		// given
		final Assessment assessment = Assessment.unlimited(1L, "test", Duration.ofMinutes(10));
		// 한 문제 등록
		assessment.replaceItems(List.of(AssessmentItem.of(1L, 10)));
		// 문항에 대한 답안 내용 (정답이 여러 개일 수 있음을 가정)
		final List<String> submittedAnswer = List.of("1", "2", "3");
		assessment.registerSubmission(1L, List.of(submittedAnswer), Duration.ofMinutes(5), true);

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

	@Test
	void 제출된_답안의_개수가_시험_문항_수와_다르면_예외가_발생한다() {
		// given
		final Assessment assessment = Assessment.unlimited(1L, "test", Duration.ofMinutes(10));
		assessment.replaceItems(List.of(AssessmentItem.of(1L, 10))); // 문항 1개

		final List<List<String>> answersWithWrongCount = List.of(List.of("1"), List.of("2")); // 답안 2개

		// when & then
		Assertions.assertThrows(AssessmentSubmissionRegisterException.class, () -> {
			assessment.registerSubmission(1L, answersWithWrongCount, Duration.ofMinutes(5), true);
		});
	}

	@Test
	void 새로운_제출물_저장시_해당_엔티티를_반환하여_ID_조회_가능하다() {
		// given
		final Assessment assessment = Assessment.unlimited(1L, "test", Duration.ofMinutes(10));
		assessment.replaceItems(List.of(AssessmentItem.of(1L, 10)));
		final List<List<String>> answersWithWrongCount = List.of(List.of("1"));

		// 제출물
		final AssessmentSubmission assessmentSubmission = assessment.registerSubmission(1L, answersWithWrongCount, Duration.ofMinutes(5), true);

		assessmentRepository.save(assessment);

		Assertions.assertNotNull(assessmentSubmission.getId());
	}
}
