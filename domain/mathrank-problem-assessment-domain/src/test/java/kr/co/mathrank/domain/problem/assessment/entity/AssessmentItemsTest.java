package kr.co.mathrank.domain.problem.assessment.entity;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import kr.co.mathrank.common.exception.MathRankException;
import kr.co.mathrank.domain.problem.assessment.exception.AssessmentItemCountNotMatchException;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentRepository;

@DataJpaTest
class AssessmentItemsTest {
	@Autowired
	private AssessmentRepository assessmentRepository;

	@Test
	void 점수의_총합이_100이_아니면_예외() {
		final Assessment assessment = Assessment.of(1L, "test", Duration.ofMinutes(1000L));

		Assertions.assertAll(
			() -> Assertions.assertThrows(MathRankException.class, () -> assessment.updateAssessmentItems(List.of(1L), List.of(99))),
			() -> Assertions.assertThrows(MathRankException.class, () -> assessment.updateAssessmentItems(List.of(1L), List.of(101)))
		);
	}

	@Test
	void 점수의_총합이_100이면_성공() {
		final Assessment assessment = Assessment.of(1L, "test", Duration.ofMinutes(1000L));

		Assertions.assertAll(
			() -> Assertions.assertDoesNotThrow(() -> assessment.updateAssessmentItems(List.of(1L), List.of(100))),
			() -> Assertions.assertDoesNotThrow(() -> assessment.updateAssessmentItems(List.of(1L, 2L), List.of(55, 45)))
		);
	}

	@Test
	void 점수갯수와_문제갯수가_일치하지_않으면_예외() {
		final Assessment assessment = Assessment.of(1L, "test", Duration.ofMinutes(1000L));

		Assertions.assertAll(
			() -> Assertions.assertThrows(AssessmentItemCountNotMatchException.class, () -> assessment.updateAssessmentItems(List.of(1L), List.of(50, 50))),
			() -> Assertions.assertThrows(AssessmentItemCountNotMatchException.class, () -> assessment.updateAssessmentItems(List.of(1L, 2L, 3L), List.of(100)))
		);
	}
}