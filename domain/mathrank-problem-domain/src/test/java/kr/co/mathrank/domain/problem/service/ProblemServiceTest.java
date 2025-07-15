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
import kr.co.mathrank.domain.problem.entity.Course;
import kr.co.mathrank.domain.problem.entity.Difficulty;
import kr.co.mathrank.domain.problem.entity.Path;
import kr.co.mathrank.domain.problem.entity.Problem;
import kr.co.mathrank.domain.problem.exception.CannotAccessProblemException;
import kr.co.mathrank.domain.problem.repository.CourseRepository;
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
	@Autowired
	private CourseRepository courseRepository;

	@ParameterizedTest
	@MethodSource("argumentsStream")
	void 형식_오류_시_에러발생(final ProblemRegisterCommand command) {
		Assertions.assertThrows(ConstraintViolationException.class, () -> problemService.save(command));
	}

	@Test
	void 업데이트_성공() {
		final Path path = new Path();
		final Course course = Course.of("test", path);
		courseRepository.save(course);
		entityManager.flush();
		entityManager.clear();

		final ProblemRegisterCommand command = new ProblemRegisterCommand(1L, "image.jpeg", "image.jpeg", AnswerType.MULTIPLE_CHOICE, path.getPath(), Difficulty.KILLER, "testCode", "answer", 1001, null);
		final Long problemId = problemService.save(command);

		final ProblemUpdateCommand updateCommand = new ProblemUpdateCommand(problemId, 1L, "newImage.jpeg", AnswerType.SHORT_ANSWER, Difficulty.KILLER, path.getPath(), "newTestCode", "newAnswer");
		problemService.update(updateCommand);

		final Problem updatedProblem = problemRepository.findById(problemId)
			.orElseThrow();
		assertEquals("newImage.jpeg", updatedProblem.getImageSource());
		assertEquals(AnswerType.SHORT_ANSWER, updatedProblem.getType());
		assertEquals(Difficulty.KILLER, updatedProblem.getDifficulty());
		assertEquals("newTestCode", updatedProblem.getSchoolCode());
		assertEquals("newAnswer", updatedProblem.getAnswer());
	}

	@Test
	void 소유자가_아니면_업데이트_불가() {
		final Path path = new Path();
		final Course course = Course.of("test", path);
		courseRepository.save(course);
		entityManager.flush();
		entityManager.clear();

		final ProblemRegisterCommand command = new ProblemRegisterCommand(1L, "image.jpeg", "image.jpeg", AnswerType.MULTIPLE_CHOICE, path.getPath(), Difficulty.KILLER, "testCode", "answer", 1001, null);
		final Long problemId = problemService.save(command);

		final ProblemUpdateCommand updateCommand = new ProblemUpdateCommand(problemId, 2L, "newImage.jpeg", AnswerType.SHORT_ANSWER, Difficulty.KILLER, path.getPath(), "newTestCode", "newAnswer");

		Assertions.assertThrows(CannotAccessProblemException.class, () -> problemService.update(updateCommand));
	}

	@Test
	void 삭제_성공() {
		final Path path = new Path();
		final Course course = Course.of("test", path);
		courseRepository.save(course);
		entityManager.flush();
		entityManager.clear();

		final ProblemRegisterCommand command = new ProblemRegisterCommand(1L, "image.jpeg", "image.jpeg", AnswerType.MULTIPLE_CHOICE, path.getPath(), Difficulty.KILLER, "testCode", "answer", 1001, null);
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
		final Path path = new Path();
		final Course course = Course.of("test", path);
		courseRepository.save(course);
		entityManager.flush();
		entityManager.clear();

		final ProblemRegisterCommand command = new ProblemRegisterCommand(1L, "image.jpeg", "image.jpeg", AnswerType.MULTIPLE_CHOICE,
			path.getPath(), Difficulty.KILLER, "testCode", "answer", 1001, null);
		final Long problemId = problemService.save(command);

		final ProblemDeleteCommand deleteCommand = new ProblemDeleteCommand(problemId, 2L);

		Assertions.assertThrows(CannotAccessProblemException.class, () -> problemService.delete(deleteCommand));
	}

	private static Stream<Arguments> argumentsStream() {
		return Stream.of(
			Arguments.of(new ProblemRegisterCommand(			null, "image.jpeg", null, AnswerType.MULTIPLE_CHOICE, "aa", Difficulty.KILLER, "testCode", "answer", 1001, null)),
			Arguments.of(new ProblemRegisterCommand(			1L, null, null, AnswerType.MULTIPLE_CHOICE, "aa", Difficulty.KILLER, "testCode", "answer", 1001, null)),
			Arguments.of(new ProblemRegisterCommand(			1L, "image.jpeg", null, null, "aa", Difficulty.KILLER, "testCode", "answer", 1001, null)),
			Arguments.of(new ProblemRegisterCommand(			1L, "image.jpeg", null, AnswerType.MULTIPLE_CHOICE, "aa", null, "testCode", "answer", 1001, null)),
			Arguments.of(new ProblemRegisterCommand(			1L, "image.jpeg", null, AnswerType.MULTIPLE_CHOICE, "aa", Difficulty.KILLER, null, "answer", 1001, null)),
			Arguments.of(new ProblemRegisterCommand(			1L, "image.jpeg", null, AnswerType.MULTIPLE_CHOICE, "aa", Difficulty.KILLER, "testCode", "", 1001, null)),
			Arguments.of(new ProblemRegisterCommand(			1L, "image.jpeg", null, AnswerType.MULTIPLE_CHOICE, "aa", Difficulty.KILLER, "testCode", null, 1001, null))
		);
	}
}
