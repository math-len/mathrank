package kr.co.mathrank.domain.problem.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.co.mathrank.domain.problem.dto.CourseRegisterCommand;
import kr.co.mathrank.domain.problem.entity.Path;
import kr.co.mathrank.domain.problem.exception.CannotFoundCourseException;
import kr.co.mathrank.domain.problem.repository.CourseRepository;

@SpringBootTest
@Transactional
class CourseServiceTest {
	@Autowired
	private CourseService courseService;
	@Autowired
	private CourseRepository courseRepository;

	@Test
	void 첫_삽입시_기본값으로_저장() {
		courseService.register(new CourseRegisterCommand("test", ""));

		Assertions.assertEquals(new Path(), courseRepository.findAll().getFirst().getPath());
	}

	@Test
	void 부모가_존재하지않으면_에러() {
		Assertions.assertThrows(
			CannotFoundCourseException.class, ()-> courseService.register(new CourseRegisterCommand("test", "12")));
	}

	@Test
	void 여러번_삽입될때_정상저장() {
		courseService.register(new CourseRegisterCommand("test", ""));
		courseService.register(new CourseRegisterCommand("test", ""));
		courseService.register(new CourseRegisterCommand("test", ""));

		Assertions.assertEquals(3, courseRepository.findAll().size());
	}
}