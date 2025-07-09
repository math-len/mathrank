package kr.co.mathrank.domain.problem.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kr.co.mathrank.domain.problem.dto.ProblemQueryCommand;
import kr.co.mathrank.domain.problem.dto.ProblemQueryPageResult;
import kr.co.mathrank.domain.problem.entity.AnswerType;
import kr.co.mathrank.domain.problem.entity.Course;
import kr.co.mathrank.domain.problem.entity.Difficulty;
import kr.co.mathrank.domain.problem.entity.Path;
import kr.co.mathrank.domain.problem.entity.Problem;
import kr.co.mathrank.domain.problem.repository.CourseRepository;
import kr.co.mathrank.domain.problem.repository.ProblemRepository;

@SpringBootTest
@Transactional
class ProblemQueryServiceTest {
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	private ProblemQueryService problemQueryService;
	@Autowired
	private ProblemRepository problemRepository;
	@Autowired
	private CourseRepository courseRepository;

	@Test
	void 문제_조회_테스트() {
		final Path path = new Path();
		final Course course = Course.of("test", path);
		courseRepository.save(course);
		entityManager.flush();
		entityManager.clear();

		// memberId가 1인 문제를 10개 등록한다.
		for (int i = 0; i < 10; i++) {
			final Problem problem = Problem.of((long) i, 1L, "문제.jpeg", Difficulty.LEVEL_FIVE, AnswerType.MULTIPLE_CHOICE, course , "1", "testCode");
			problemRepository.save(problem);
		}

		// memberId가 1인 사용자의 문제를 조회한다.
		final ProblemQueryPageResult result = problemQueryService.query(new ProblemQueryCommand(1L, null, null, null, 2, 1));

		// 조회된 문제의 갯수는 2개이고, 전체 페이지는 5페이지이다. ( possibleNextPageNumbers() 가 4개)
		Assertions.assertEquals(2, result.queryResults().size());
		Assertions.assertEquals(4, result.possibleNextPageNumbers().size());
	}
}