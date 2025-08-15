package kr.co.mathrank.domain.problem.assessment.entity;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentRepository;

@SpringBootTest(properties = """
	spring.jpa.show-sql=true
	spring.jpa.properties.hibernate.format_sql=true
	""")
class AssessmentTest {
	@Autowired
	private AssessmentRepository assessmentRepository;
	@PersistenceContext
	private EntityManager entityManager;

	@Test
	void 시험지_생성시_문항들은_1번부터_순서대로_등록된다() {
		final Assessment assessment = Assessment.of(1L, "test");
		// 시험 문제 번호
		assessment.setAssessmentItems(List.of(345L, 212L, 120232L, 1221323L));

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
		final Assessment assessment = Assessment.of(1L, "test");
		// 바꾸기 전 시험 문제 번호
		assessment.setAssessmentItems(List.of(345L, 212L, 120232L, 1221323L));

		// 새로운 문제들로 변경
		assessment.setAssessmentItems(List.of(1212L, 2222L, 3333L));

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
		final Assessment assessment = Assessment.of(1L, "test");
		// 시험 문제 번호
		assessment.setAssessmentItems(List.of(345L, 212L, 120232L, 1221323L));

		// 새로운 엔티티DB 저장
		assessmentRepository.save(assessment);
		entityManager.flush();
		entityManager.clear();

		// 저장된 엔티티 가져와서 업데이트
		final Long id = assessment.getId();
		final Assessment reloadedAssessment = assessmentRepository.findById(id).orElseThrow();
		reloadedAssessment.setAssessmentItems(List.of(1L, 2L, 3L));
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
