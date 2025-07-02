package kr.co.mathrank.domain.problem.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintViolationException;
import kr.co.mathrank.domain.problem.dto.ProblemDeleteCommand;
import kr.co.mathrank.domain.problem.dto.ProblemRegisterCommand;
import kr.co.mathrank.domain.problem.dto.ProblemUpdateCommand;
import kr.co.mathrank.domain.problem.entity.AnswerType;
import kr.co.mathrank.domain.problem.entity.Difficulty;
import kr.co.mathrank.domain.problem.entity.Problem;
import kr.co.mathrank.domain.problem.entity.ProblemCourse;
import kr.co.mathrank.domain.problem.repository.ProblemRepository;

@SpringBootTest(properties = """
logging.level.kr.co.mathrank=DEBUG""")
@Transactional
class ProblemServiceTest {
	@Autowired
	private ProblemService problemService;
	@Autowired
	private ProblemRepository problemRepository;
	@PersistenceContext
	private EntityManager entityManager;

	@ParameterizedTest
	@MethodSource("argumentsStream")
	void 형식_오류_시_에러발생(final ProblemRegisterCommand command) {
		Assertions.assertThrows(ConstraintViolationException.class, () -> problemService.save(command));
	}

	@Test
	void 업데이트_성공() {
		final ProblemRegisterCommand command = new ProblemRegisterCommand(1L, "image.jpeg", AnswerType.MULTIPLE_CHOICE, Difficulty.LEVEL_FIVE, "testCode", "answer", ProblemCourse.ELEMENTARY);
		final Long problemId = problemService.save(command);

		final ProblemUpdateCommand updateCommand = new ProblemUpdateCommand(problemId, 1L, "newImage.jpeg", AnswerType.SHORT_ANSWER, Difficulty.LEVEL_THREE, "newTestCode", "newAnswer", ProblemCourse.HIGH);
		problemService.update(updateCommand);

		final Problem updatedProblem = problemRepository.findById(problemId)
			.orElseThrow();
		assertEquals("newImage.jpeg", updatedProblem.getImageSource());
		assertEquals(AnswerType.SHORT_ANSWER, updatedProblem.getType());
		assertEquals(Difficulty.LEVEL_THREE, updatedProblem.getDifficulty());
		assertEquals("newTestCode", updatedProblem.getSchoolCode());
		assertEquals("newAnswer", updatedProblem.getAnswer());
		assertEquals(ProblemCourse.HIGH, updatedProblem.getProblemCourse());
	}

	@Test
	void 소유자가_아니면_업데이트_불가() {
		final ProblemRegisterCommand command = new ProblemRegisterCommand(1L, "image.jpeg", AnswerType.MULTIPLE_CHOICE, Difficulty.LEVEL_FIVE, "testCode", "answer", ProblemCourse.ELEMENTARY);
		final Long problemId = problemService.save(command);

		final ProblemUpdateCommand updateCommand = new ProblemUpdateCommand(problemId, 2L, "newImage.jpeg", AnswerType.SHORT_ANSWER, Difficulty.LEVEL_THREE, "newTestCode", "newAnswer", ProblemCourse.HIGH);

		Assertions.assertThrows(IllegalArgumentException.class, () -> problemService.update(updateCommand));
	}

	@Test
	void 삭제_성공() {
		final ProblemRegisterCommand command = new ProblemRegisterCommand(1L, "image.jpeg", AnswerType.MULTIPLE_CHOICE, Difficulty.LEVEL_FIVE, "testCode", "answer", ProblemCourse.ELEMENTARY);
		final Long problemId = problemService.save(command);

		entityManager.flush();
		entityManager.clear();

		final ProblemDeleteCommand deleteCommand = new ProblemDeleteCommand(problemId, 1L);
		problemService.delete(deleteCommand);

		entityManager.flush();
		entityManager.clear();

		assertFalse(problemRepository.findById(problemId).isPresent());
	}

	@Test
	void 본인_문제만_삭제가능() {
		final ProblemRegisterCommand command = new ProblemRegisterCommand(1L, "image.jpeg", AnswerType.MULTIPLE_CHOICE, Difficulty.LEVEL_FIVE, "testCode", "answer", ProblemCourse.ELEMENTARY);
		final Long problemId = problemService.save(command);

		final ProblemDeleteCommand deleteCommand = new ProblemDeleteCommand(problemId, 2L);

		Assertions.assertThrows(IllegalArgumentException.class, () -> problemService.delete(deleteCommand));
	}

	private static Stream<Arguments> argumentsStream() {
		return Stream.of(
			Arguments.of(new ProblemRegisterCommand(			null, "image.jpeg", AnswerType.MULTIPLE_CHOICE, Difficulty.LEVEL_FIVE, "testCode", "answer", ProblemCourse.ELEMENTARY)),
			Arguments.of(new ProblemRegisterCommand(			1L, null, AnswerType.MULTIPLE_CHOICE, Difficulty.LEVEL_FIVE, "testCode", "answer", ProblemCourse.ELEMENTARY)),
			Arguments.of(new ProblemRegisterCommand(			1L, "image.jpeg", null, Difficulty.LEVEL_FIVE, "testCode", "answer", ProblemCourse.ELEMENTARY)),
			Arguments.of(new ProblemRegisterCommand(			1L, "image.jpeg", AnswerType.MULTIPLE_CHOICE, null, "testCode", "answer", ProblemCourse.ELEMENTARY)),
			Arguments.of(new ProblemRegisterCommand(			1L, "image.jpeg", AnswerType.MULTIPLE_CHOICE, Difficulty.LEVEL_FIVE, null, "answer", ProblemCourse.ELEMENTARY)),
			Arguments.of(new ProblemRegisterCommand(			1L, "image.jpeg", AnswerType.MULTIPLE_CHOICE, Difficulty.LEVEL_FIVE, "testCode", "", ProblemCourse.ELEMENTARY)),
			Arguments.of(new ProblemRegisterCommand(			1L, "image.jpeg", AnswerType.MULTIPLE_CHOICE, Difficulty.LEVEL_FIVE, "testCode", null, ProblemCourse.ELEMENTARY)),
			Arguments.of(new ProblemRegisterCommand(			1L, "image.jpeg", AnswerType.MULTIPLE_CHOICE, Difficulty.LEVEL_FIVE, "testCode", "answer", null))
		);
	}
}
