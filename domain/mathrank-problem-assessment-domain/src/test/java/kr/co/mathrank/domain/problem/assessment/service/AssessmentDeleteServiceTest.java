package kr.co.mathrank.domain.problem.assessment.service;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kr.co.mathrank.domain.problem.assessment.entity.Assessment;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentItem;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentItemRepository;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentRepository;

@SpringBootTest(properties = """
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
""")
class AssessmentDeleteServiceTest {
	@Autowired
	private AssessmentDeleteService assessmentDeleteService;
	@Autowired
	private AssessmentRepository assessmentRepository;
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	private AssessmentItemRepository assessmentItemRepository;

	@Test
	@Transactional
	void 문제가_여러개인_문제집에서_원본문제가_삭제됐을때_문제집이_삭제된다() {
		final Assessment assessment = Assessment.unlimited(1L, "name", Duration.ofHours(1L));
		assessment.replaceItems(List.of(
			AssessmentItem.of(1L, 20),
			AssessmentItem.of(2L, 80)
		));

		assessmentRepository.save(assessment);

		entityManager.flush();
		entityManager.clear();

		// 속한 문제 하나 삭제한다
		assessmentDeleteService.deleteByProblemId(1L);

		entityManager.flush();
		entityManager.clear();

		// 삭제된 문제가 속하는 시험지는 모두 삭제한다
		Assertions.assertEquals(0, assessmentRepository.count());
		Assertions.assertEquals(0, assessmentItemRepository.count());
	}
}
