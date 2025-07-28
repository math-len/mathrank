package kr.co.mathrank.domain.problem.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kr.co.mathrank.domain.problem.entity.Answer;
import kr.co.mathrank.domain.problem.entity.AnswerType;
import kr.co.mathrank.domain.problem.entity.Course;
import kr.co.mathrank.domain.problem.entity.Difficulty;
import kr.co.mathrank.domain.problem.entity.Path;
import kr.co.mathrank.domain.problem.entity.Problem;

@SpringBootTest
@Transactional
class ProblemRepositoryTest {
	@Autowired
	private ProblemRepository problemRepository;
	@Autowired
	private CourseRepository courseRepository;
	@PersistenceContext
	private EntityManager entityManager;

	@Test
	void 본인_문제_조회_테스트() {
		final Path path = new Path();
		final Course course = Course.of("test", path);
		courseRepository.save(course);
		entityManager.flush();
		entityManager.clear();

		// 사용자 1의 문제
		final Problem owner1 = Problem.of((long) 1, 2L, "문제.jpeg", Difficulty.KILLER, AnswerType.MULTIPLE_CHOICE, course, "testCode", null, null,
			1001);
		// 사용자 2의 문제
		final Problem owner2 = Problem.of((long) 2, 1L, "문제.jpeg", Difficulty.KILLER, AnswerType.MULTIPLE_CHOICE, course, "testCode", null, null, 1001);

		problemRepository.save(owner1);
		problemRepository.save(owner2);

		final List<Problem> problems = problemRepository.query(1L, null, null, null, null, null, null, 1001, null, 10,
			1);

		Assertions.assertEquals(1, problems.size());
	}

	@Test
	void 문제ID_로_조회된다() {
		final Path path = new Path();
		final Course course = Course.of("test", path);
		courseRepository.save(course);
		entityManager.flush();
		entityManager.clear();

		final Long problemId = 1021923L;

		// 사용자 1의 문제
		final Problem problem = Problem.of(problemId, 2L, "문제.jpeg", Difficulty.KILLER, AnswerType.MULTIPLE_CHOICE, course, "testCode", null, null,
			1001);

		problemRepository.save(problem);

		final List<Problem> problems = problemRepository.query(null, problemId, null, null, null, null, null, 1001,
			null, 10, 1);

		Assertions.assertEquals(1, problems.size());
	}

	@Test
	void 조건_없을때_모두_조회한다() {
		final Path path = new Path();
		final Course course = Course.of("test", path);
		courseRepository.save(course);
		entityManager.flush();
		entityManager.clear();

		final Problem problem1 = Problem.of(1L, 2L, "문제.jpeg", Difficulty.KILLER, AnswerType.MULTIPLE_CHOICE, course, "testCode", null, null, 1001);
		final Problem problem2 = Problem.of(2L, 2L, "문제.jpeg", Difficulty.KILLER, AnswerType.MULTIPLE_CHOICE, course, "testCode", null, null, 1001);

		problemRepository.save(problem1);
		problemRepository.save(problem2);

		final List<Problem> problems = problemRepository.query(null, null, null, null, null, null, null, 1001, null, 10,
			1);

		Assertions.assertEquals(2, problems.size());
	}

	@Test
	void 단일_조건_으로_조회된다() {
		final Path path = new Path();
		final Course course = Course.of("test", path);
		courseRepository.save(course);
		entityManager.flush();
		entityManager.clear();

		// level 4
		final Problem problem1 = Problem.of(1L, 2L, "문제.jpeg", Difficulty.HIGH, AnswerType.MULTIPLE_CHOICE, course, "testCode", null, null, 1001);

		// level 5
		final Problem problem2 = Problem.of(2L, 2L, "문제.jpeg", Difficulty.KILLER, AnswerType.MULTIPLE_CHOICE, course, "testCode", null, null, 1001);

		problemRepository.save(problem1);
		problemRepository.save(problem2);

		final List<Problem> problems = problemRepository.query(null, null, Difficulty.KILLER, Difficulty.KILLER, null,
			null, null, 1001, null, 10, 1);

		Assertions.assertEquals(1, problems.size());
	}

	@Test
	void 여러조건으로_조회된다() {
		final Path path = new Path();
		final Course course = Course.of("test", path);
		courseRepository.save(course);
		entityManager.flush();
		entityManager.clear();

		final Problem problem1 = Problem.of(1L, 1L, "문제.jpeg", Difficulty.KILLER, AnswerType.MULTIPLE_CHOICE, course, "testCode", null, null, 1001);
		final Problem problem2 = Problem.of(2L, 3L, "문제.jpeg", Difficulty.KILLER, AnswerType.MULTIPLE_CHOICE, course, "testCode", null, null, 1001);

		problemRepository.save(problem1);
		problemRepository.save(problem2);

		final List<Problem> problems = problemRepository.query(1L, null, Difficulty.KILLER, Difficulty.KILLER,
			AnswerType.MULTIPLE_CHOICE,
			"aa", null, 1001, null, 10, 1);

		Assertions.assertEquals(1, problems.size());
	}

	@Test
	void 일치하는게_없을땐_빈_리스트를_반환한다() {
		final Path path = new Path();
		final Course course = Course.of("test", path);
		courseRepository.save(course);
		entityManager.flush();
		entityManager.clear();

		final Problem problem1 = Problem.of(1L, 2L, "문제.jpeg", Difficulty.KILLER, AnswerType.MULTIPLE_CHOICE, course, "testCode", null, null, 1001);
		final Problem problem2 = Problem.of(2L, 3L, "문제.jpeg", Difficulty.KILLER, AnswerType.MULTIPLE_CHOICE, course, "testCode", null, null, 1001);

		problemRepository.save(problem1);
		problemRepository.save(problem2);

		final List<Problem> problems = problemRepository.query(null, null, Difficulty.HIGH, Difficulty.HIGH, null, null,
			null, 1001, null, 10, 1);

		Assertions.assertTrue(problems.isEmpty());
	}

	@Test
	void 조건_없을때_모든_문제를_조회한다() {
		final Path path = new Path();
		final Course course = Course.of("test", path);
		courseRepository.save(course);
		entityManager.flush();
		entityManager.clear();

		for (int i = 0; i < 10; i++) {
			final Problem problem = Problem.of((long) i, 1L, "문제.jpeg", Difficulty.KILLER, AnswerType.MULTIPLE_CHOICE, course, "testCode", null, null, 1001);
			problemRepository.save(problem);
		}

		assertEquals(10, problemRepository.count(null, null, null, null, null, null, null, 1001, null));
	}

	@Test
	void 특정_조건의_문제_총_갯수_조회() {
		final Path path = new Path();
		final Course course = Course.of("test", path);
		courseRepository.save(course);
		entityManager.flush();
		entityManager.clear();

		// level 5 문제들
		for (int i = 0; i < 10; i++) {
			final Problem problem = Problem.of((long) i, 1L, "문제.jpeg", Difficulty.HIGH, AnswerType.MULTIPLE_CHOICE, course, "testCode", null, null, 1001);
			problemRepository.save(problem);
		}
		// level1 문제들
		for (int i = 10; i < 20; i++) {
			final Problem problem = Problem.of((long) i, 1L, "문제.jpeg", Difficulty.KILLER, AnswerType.MULTIPLE_CHOICE, course, "testCode", null, null, 1001);
			problemRepository.save(problem);
		}

		assertEquals(10,
			problemRepository.count(null, null, Difficulty.KILLER, Difficulty.KILLER, null, null, null, 1001, null));
	}

	@Test
	void 조건_모두_널일때_모든_갯수_조회() {
		final Path path = new Path();
		final Course course = Course.of("test", path);
		courseRepository.save(course);
		entityManager.flush();
		entityManager.clear();

		// level 5 문제들
		for (int i = 0; i < 10; i++) {
			final Problem problem = Problem.of((long) i, 1L, "문제.jpeg", Difficulty.KILLER, AnswerType.MULTIPLE_CHOICE, course, "testCode", null, null, 1001);
			problemRepository.save(problem);
		}
		// level1 문제들
		for (int i = 10; i < 20; i++) {
			final Problem problem = Problem.of((long) i, 1L, "문제.jpeg", Difficulty.KILLER, AnswerType.MULTIPLE_CHOICE, course, "testCode", null, null, 1001);
			problemRepository.save(problem);
		}

		assertEquals(20, problemRepository.count(null, null, null, null, null, null, null, 1001, null));
	}

	@Test
	void 하위_단원까지_함께_조회된다() {
		final Path path = new Path();
		final Course parentCourse = Course.of("test", path);
		courseRepository.save(parentCourse);
		final Course childCourse = Course.of("test2", path.nextChild(path));
		courseRepository.save(childCourse);

		entityManager.flush();
		entityManager.clear();

		for (int i = 0; i < 10; i++) {
			final Problem problem = Problem.of((long) i, 1L, "문제.jpeg", Difficulty.KILLER, AnswerType.MULTIPLE_CHOICE, parentCourse, "testCode", null, null, 1001);
			problemRepository.save(problem);
		}

		for (int i = 10; i < 20; i++) {
			final Problem problem = Problem.of((long) i, 1L, "문제.jpeg", Difficulty.KILLER, AnswerType.MULTIPLE_CHOICE, childCourse, "testCode", null, null, 1001);
			problemRepository.save(problem);
		}

		Assertions.assertEquals(20,
			problemRepository.query(null, null, null, null, null, path.getPath(), null, 1001, null, 100, 1).size());
	}

	@Test
	void 질문_페이징시_limit을_활용하여_두번의_쿼리로_해결한다() {
		final Course course = Course.of("test2", new Path());
		courseRepository.save(course);

		entityManager.flush();
		entityManager.clear();

		// 데이터 삽입 ( 문제당 답안 2개씩 )
		for (int i = 0; i < 20; i++) {
			final Problem problem = Problem.of((long) i, 1L, "문제.jpeg", Difficulty.KILLER, AnswerType.MULTIPLE_CHOICE, course, "testCode", null, null, 1001);
			final Set<Answer> answers = Set.of(Answer.of((long) i, "1", problem), Answer.of((long) i + 20, "2", problem));
			problem.setAnswers(answers);
			problemRepository.save(problem);
		}

		entityManager.flush();
		entityManager.clear();

		// 5개 조회
		final List<Problem> problems = problemRepository.query(1L, null, null, null, null, null, null, 1001, null, 5, 1);
		// 로그를 확인했을 때, limit과 offset을 활용하는것을 확인
		Assertions.assertEquals(5, problems.size());
	}
}
