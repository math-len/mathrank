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

import jakarta.validation.ConstraintViolationException;
import kr.co.mathrank.domain.problem.dto.ProblemRegisterCommand;
import kr.co.mathrank.domain.problem.entity.AnswerType;
import kr.co.mathrank.domain.problem.entity.Difficulty;
import kr.co.mathrank.domain.problem.entity.ProblemCourse;

@SpringBootTest
class ProblemServiceTest {
	@Autowired
	private ProblemService problemService;

	@ParameterizedTest
	@MethodSource("argumentsStream")
	void 형식_오류_시_에러발생(final ProblemRegisterCommand command) {
		Assertions.assertThrows(ConstraintViolationException.class, () -> problemService.save(command));
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
