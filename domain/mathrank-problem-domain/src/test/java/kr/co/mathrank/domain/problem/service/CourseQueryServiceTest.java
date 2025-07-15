package kr.co.mathrank.domain.problem.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.co.mathrank.domain.problem.entity.Course;
import kr.co.mathrank.domain.problem.entity.Path;
import kr.co.mathrank.domain.problem.repository.CourseRepository;

@SpringBootTest
@Transactional
class CourseQueryServiceTest {
	@Autowired
	private CourseRepository courseRepository;
	@Autowired
	private CourseQueryService courseQueryService;

	@Test
	void test() {
		final Path path = new Path();
		final Path child = path.nextChild(path);
		final Path grandChild = child.nextChild(child);

		courseRepository.save(Course.of("test", path));
		courseRepository.save(Course.of("test", child));
		courseRepository.save(Course.of("test", grandChild));

		Assertions.assertEquals(child.getPath(), courseQueryService.queryChildes(path.getPath()).results().getFirst().coursePath());
		Assertions.assertEquals(1, courseQueryService.queryChildes(path.getPath()).results().size());
	}
}