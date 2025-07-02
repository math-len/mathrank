package kr.co.mathrank.domain.problem.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.co.mathrank.domain.problem.repository.ProblemRepository;

@SpringBootTest
@Transactional
class ProblemTest {
	@Autowired
	private ProblemRepository problemRepository;

	@Test
	void 영속화될때_새로운_엔티티로_판단한다() {
		final Problem problem = Problem.of(1L, 2L, "문제.jpeg", Difficulty.LEVEL_FIVE, AnswerType.MULTIPLE_CHOICE, ProblemCourse.ELEMENTARY , "1", "testCode");
		final Problem savedProblem = problemRepository.save(problem);

		// save 시, merge 면 새로운 엔티티를 반환한다.
		// persist이면, 영속화된 엔티티를 그대로 반환한다.
		Assertions.assertEquals(problem, savedProblem);
	}
}
