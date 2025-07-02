package kr.co.mathrank.domain.problem.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.co.mathrank.domain.problem.entity.AnswerType;
import kr.co.mathrank.domain.problem.entity.Difficulty;
import kr.co.mathrank.domain.problem.entity.Problem;
import kr.co.mathrank.domain.problem.entity.ProblemCourse;

@SpringBootTest
@Transactional
class ProblemRepositoryTest {
	@Autowired
	private ProblemRepository problemRepository;

	@Test
	void 본인_문제_조회_테스트() {
		// 사용자 1의 문제
		final Problem owner1 = Problem.of((long) 1, 2L, "문제.jpeg", Difficulty.LEVEL_FIVE, AnswerType.MULTIPLE_CHOICE, ProblemCourse.ELEMENTARY , "1", "testCode");
		// 사용자 2의 문제
		final Problem owner2 = Problem.of((long) 2, 1L, "문제.jpeg", Difficulty.LEVEL_FIVE, AnswerType.MULTIPLE_CHOICE, ProblemCourse.ELEMENTARY , "1", "testCode");

		problemRepository.save(owner1);
		problemRepository.save(owner2);

		final List<Problem> problems = problemRepository.query(1L, null, null, null, 10, 1);

		Assertions.assertEquals(1, problems.size());
	}

	@Test
	void 조건_없을때_모두_조회한다() {
		final Problem problem1 = Problem.of(1L, 2L, "문제.jpeg", Difficulty.LEVEL_FIVE, AnswerType.MULTIPLE_CHOICE, ProblemCourse.ELEMENTARY , "1", "testCode");
		final Problem problem2 = Problem.of(2L, 2L, "문제.jpeg", Difficulty.LEVEL_FIVE, AnswerType.MULTIPLE_CHOICE, ProblemCourse.ELEMENTARY , "1", "testCode");

		problemRepository.save(problem1);
		problemRepository.save(problem2);

		final List<Problem> problems = problemRepository.query(null, null, null, null, 10, 1);

		Assertions.assertEquals(2, problems.size());
	}

	@Test
	void 단일_조건_으로_조회된다() {
		// level 4
		final Problem problem1 = Problem.of(1L, 2L, "문제.jpeg", Difficulty.LEVEL_FOUR, AnswerType.MULTIPLE_CHOICE, ProblemCourse.ELEMENTARY , "1", "testCode");

		// level 5
		final Problem problem2 = Problem.of(2L, 2L, "문제.jpeg", Difficulty.LEVEL_FIVE, AnswerType.MULTIPLE_CHOICE, ProblemCourse.ELEMENTARY , "1", "testCode");

		problemRepository.save(problem1);
		problemRepository.save(problem2);

		final List<Problem> problems = problemRepository.query(null, Difficulty.LEVEL_FIVE, null, null, 10, 1);

		Assertions.assertEquals(1, problems.size());
	}

	@Test
	void 여러조건으로_조회된다() {
		final Problem problem1 = Problem.of(1L, 1L, "문제.jpeg", Difficulty.LEVEL_FIVE, AnswerType.MULTIPLE_CHOICE, ProblemCourse.MIDDLE , "1", "testCode");
		final Problem problem2 = Problem.of(2L, 3L, "문제.jpeg", Difficulty.LEVEL_FIVE, AnswerType.MULTIPLE_CHOICE, ProblemCourse.ELEMENTARY , "1", "testCode");

		problemRepository.save(problem1);
		problemRepository.save(problem2);

		final List<Problem> problems = problemRepository.query(1L, Difficulty.LEVEL_FIVE, AnswerType.MULTIPLE_CHOICE,
			ProblemCourse.MIDDLE, 10, 1);

		Assertions.assertEquals(1, problems.size());
	}

	@Test
	void 일치하는게_없을땐_빈_리스트를_반환한다() {
		final Problem problem1 = Problem.of(1L, 2L, "문제.jpeg", Difficulty.LEVEL_FIVE, AnswerType.MULTIPLE_CHOICE, ProblemCourse.ELEMENTARY , "1", "testCode");
		final Problem problem2 = Problem.of(2L, 3L, "문제.jpeg", Difficulty.LEVEL_FIVE, AnswerType.MULTIPLE_CHOICE, ProblemCourse.ELEMENTARY , "1", "testCode");

		problemRepository.save(problem1);
		problemRepository.save(problem2);

		final List<Problem> problems = problemRepository.query(null, Difficulty.LEVEL_THREE, null, null, 10, 1);

		Assertions.assertTrue(problems.isEmpty());
	}

	@Test
	void 조건_없을때_모든_문제를_조회한다() {
		for (int i = 0; i < 10; i++) {
			final Problem problem = Problem.of((long) i, 1L, "문제.jpeg", Difficulty.LEVEL_FIVE, AnswerType.MULTIPLE_CHOICE, ProblemCourse.ELEMENTARY , "1", "testCode");
			problemRepository.save(problem);
		}

		assertEquals(10, problemRepository.count(null, null, null, null));
	}

	@Test
	void 특정_조건의_문제_총_갯수_조회() {
		// level 5 문제들
		for (int i = 0; i < 10; i++) {
			final Problem problem = Problem.of((long) i, 1L, "문제.jpeg", Difficulty.LEVEL_FIVE, AnswerType.MULTIPLE_CHOICE, ProblemCourse.ELEMENTARY , "1", "testCode");
			problemRepository.save(problem);
		}
		// level1 문제들
		for (int i = 10; i < 20; i++) {
			final Problem problem = Problem.of((long) i, 1L, "문제.jpeg", Difficulty.LEVEL_ONE, AnswerType.MULTIPLE_CHOICE, ProblemCourse.ELEMENTARY , "1", "testCode");
			problemRepository.save(problem);
		}

		assertEquals(10, problemRepository.count(null, Difficulty.LEVEL_FIVE, null, null));
	}

	@Test
	void 조건_모두_널일때_모든_갯수_조회() {
		// level 5 문제들
		for (int i = 0; i < 10; i++) {
			final Problem problem = Problem.of((long) i, 1L, "문제.jpeg", Difficulty.LEVEL_FIVE, AnswerType.MULTIPLE_CHOICE, ProblemCourse.ELEMENTARY , "1", "testCode");
			problemRepository.save(problem);
		}
		// level1 문제들
		for (int i = 10; i < 20; i++) {
			final Problem problem = Problem.of((long) i, 1L, "문제.jpeg", Difficulty.LEVEL_ONE, AnswerType.MULTIPLE_CHOICE, ProblemCourse.ELEMENTARY , "1", "testCode");
			problemRepository.save(problem);
		}

		assertEquals(20, problemRepository.count(null, null, null, null));
	}
}