package kr.co.mathrank.domain.problem.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import kr.co.mathrank.domain.problem.entity.Course;
import kr.co.mathrank.domain.problem.entity.Path;

@SpringBootTest
@Transactional
class CourseRepositoryTest {
	@Autowired
	private CourseRepository courseRepository;

	@Test
	void 가장_긴놈을_찾는다() {
		final Path path = new Path();
		courseRepository.save(Course.of("test1", path));
		courseRepository.save(Course.of("test2", path.nextChild(path)));

		Assertions.assertEquals("aaaa", courseRepository.queryCourseStartsWith(path.getPath(),
				PageRequest.ofSize(1).withSort(Sort.by(Sort.Direction.DESC, "path")))
			.getFirst().getPath().getPath());
	}
}
