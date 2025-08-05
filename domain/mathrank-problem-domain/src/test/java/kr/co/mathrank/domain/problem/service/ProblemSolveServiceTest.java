package kr.co.mathrank.domain.problem.service;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.co.mathrank.domain.problem.core.AnswerType;
import kr.co.mathrank.domain.problem.core.Difficulty;
import kr.co.mathrank.domain.problem.dto.ProblemRegisterCommand;
import kr.co.mathrank.domain.problem.dto.ProblemSolveCommand;
import kr.co.mathrank.domain.problem.dto.ProblemSolveResult;
import kr.co.mathrank.domain.problem.entity.Course;
import kr.co.mathrank.domain.problem.entity.Path;
import kr.co.mathrank.domain.problem.repository.CourseRepository;
import lombok.RequiredArgsConstructor;

@SpringBootTest
@Transactional
@RequiredArgsConstructor
class ProblemSolveServiceTest {
	@Autowired
	private ProblemSolveService problemSolveService;
	@Autowired
	private ProblemService problemService;
	@Autowired
	private CourseRepository courseRepository;

	@Test
	void 제출된_정답이_실제_정답이랑_정확히_일치할때_성공한다() {
		final Path path = new Path();
		final Course course = Course.of("test", path);
		courseRepository.save(course);

		final ProblemRegisterCommand command = new ProblemRegisterCommand(1L, "image.jpeg", "image.jpeg", AnswerType.MULTIPLE_CHOICE, path.getPath(), Difficulty.KILLER, "testCode",
			Set.of("1"), 1001, null);
		final Long problemId = problemService.save(command);

		// 실제 정답 : 1
		// 제출 정답 : 1
		final ProblemSolveResult result = problemSolveService.solve(new ProblemSolveCommand(problemId, List.of(String.valueOf(1))));

		Assertions.assertTrue(result.success());
	}

	@Test
	void 제출된_정답이_일치하지만_더많으면_실패한다() {
		final Path path = new Path();
		final Course course = Course.of("test", path);
		courseRepository.save(course);

		final ProblemRegisterCommand command = new ProblemRegisterCommand(1L, "image.jpeg", "image.jpeg", AnswerType.MULTIPLE_CHOICE, path.getPath(), Difficulty.KILLER, "testCode",
			Set.of("1"), 1001, null);
		final Long problemId = problemService.save(command);

		// 정답인 1에, 추가로 2까지 포함하여 제출한다
		// 실제 정답 : 1
		// 제출 정답 : 1, 2
		final ProblemSolveResult result = problemSolveService.solve(new ProblemSolveCommand(problemId, List.of(String.valueOf(1), String.valueOf(2))));

		Assertions.assertFalse(result.success());
	}

	@Test
	void 제출된_정답에_정답이_포함되지_않으면_실패한다() {
		final Path path = new Path();
		final Course course = Course.of("test", path);
		courseRepository.save(course);

		final ProblemRegisterCommand command = new ProblemRegisterCommand(1L, "image.jpeg", "image.jpeg", AnswerType.MULTIPLE_CHOICE, path.getPath(), Difficulty.KILLER, "testCode",
			Set.of("1"), 1001, null);
		final Long problemId = problemService.save(command);


		// 정답인 1을 미포함
		// 실제 정답 : 1
		// 제출 정답 : 2
		final ProblemSolveResult result = problemSolveService.solve(new ProblemSolveCommand(problemId, List.of(String.valueOf(2))));

		Assertions.assertFalse(result.success());
	}
}
