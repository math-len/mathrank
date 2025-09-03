package kr.co.mathrank.domain.problem.assessment.entity;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kr.co.mathrank.common.querydsl.MathrankQueryDslConfiguration;
import kr.co.mathrank.domain.problem.assessment.exception.SubmissionTimeExceedException;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentRepository;

@DataJpaTest
@Import(MathrankQueryDslConfiguration.class)
class AssessmentTest {
	@Autowired
	private AssessmentRepository assessmentRepository;
	@PersistenceContext
	private EntityManager entityManager;

	@Test
	void 답안지_제출시간이_초과되면_제출할_수_없다() {
		final Assessment assessment = Assessment.unlimited(1L, "test", Duration.ofMinutes(100L));

		Assertions.assertThrows(SubmissionTimeExceedException.class,
			() -> assessment.registerSubmission(1L, Collections.emptyList(), Duration.ofMinutes(101L), true));
	}

	@Test
	void 시험지_생성시_문항들은_1번부터_순서대로_등록된다() {
		final Assessment assessment = Assessment.unlimited(1L, "test", Duration.ofMinutes(100L));
		// 시험 문제 번호
		assessment.replaceItems(
			List.of(
				AssessmentItem.of(1L, 25),
				AssessmentItem.of(1L, 25),
				AssessmentItem.of(1L, 25),
				AssessmentItem.of(1L, 25))
		);

		Assertions.assertAll(
			// 시험문제 갯수 확인
			() -> Assertions.assertEquals(4, assessment.getAssessmentItems().size()),

			// 문항 번호 확인
			() -> Assertions.assertEquals(1, assessment.getAssessmentItems().get(0).getSequence()),
			() -> Assertions.assertEquals(2, assessment.getAssessmentItems().get(1).getSequence()),
			() -> Assertions.assertEquals(3, assessment.getAssessmentItems().get(2).getSequence()),
			() -> Assertions.assertEquals(4, assessment.getAssessmentItems().get(3).getSequence())
		);
	}

	@Test
	void 시험지내_문제_수정은_전체를_바꿔야한다() {
		final Assessment assessment = Assessment.unlimited(1L, "test", Duration.ofMinutes(100L));
		// 바꾸기 전 시험 문제 번호
		assessment.replaceItems(List.of(
			AssessmentItem.of(1L, 25),
			AssessmentItem.of(1L, 25),
			AssessmentItem.of(1L, 25),
			AssessmentItem.of(1L, 25)));

		// 새로운 문제들로 변경
		assessment.replaceItems(List.of(
			AssessmentItem.of(1L, 25),
			AssessmentItem.of(1L, 25),
			AssessmentItem.of(1L, 25)
		));

		Assertions.assertAll(
			// 시험문제 갯수 확인
			() -> Assertions.assertEquals(3, assessment.getAssessmentItems().size()),

			// 문항 번호 확인
			() -> Assertions.assertEquals(1, assessment.getAssessmentItems().get(0).getSequence()),
			() -> Assertions.assertEquals(2, assessment.getAssessmentItems().get(1).getSequence()),
			() -> Assertions.assertEquals(3, assessment.getAssessmentItems().get(2).getSequence())
		);
	}

	@Test
	@Transactional
	void 트랜잭션_안에서_문항_변경시_DB_정상반영() {
		final Assessment assessment = Assessment.unlimited(1L, "test", Duration.ofMinutes(100L));
		// 시험 문제 번호
		assessment.replaceItems(
			List.of(
				AssessmentItem.of(1L, 25),
				AssessmentItem.of(1L, 25),
				AssessmentItem.of(1L, 25),
				AssessmentItem.of(1L, 25))
		);

		// 새로운 엔티티DB 저장
		assessmentRepository.save(assessment);
		entityManager.flush();
		entityManager.clear();

		// 저장된 엔티티 가져와서 업데이트
		final Long id = assessment.getId();
		final Assessment reloadedAssessment = assessmentRepository.findById(id).orElseThrow();
		reloadedAssessment.replaceItems(
			List.of(
				AssessmentItem.of(1L, 25),
				AssessmentItem.of(2L, 25),
				AssessmentItem.of(3L, 50)
			)
		);
		entityManager.flush();
		entityManager.clear();

		// 저장된 값 확인
		final Assessment last = assessmentRepository.findById(id).orElseThrow();
		Assertions.assertAll(
			() -> Assertions.assertEquals(3, last.getAssessmentItems().size()),

			// 저장된 problemId 확인
			() -> Assertions.assertIterableEquals(
				List.of(1L, 2L, 3L),
				last.getAssessmentItems().stream().map(AssessmentItem::getProblemId).toList()),

			// 저장된 문제들의 순서 확인
			() -> Assertions.assertIterableEquals(
				List.of(1, 2, 3),
				last.getAssessmentItems().stream().map(AssessmentItem::getSequence).toList()
			)
		);
	}
}
