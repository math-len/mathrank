package kr.co.mathrank.domain.problem.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.co.mathrank.domain.problem.dto.CourseQueryContainsParentsResult;
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

	@Test
	void 부모경로까지_조회하기() {
		final Path path = new Path();
		final Path child = path.nextChild(path);
		final Path grandChild = child.nextChild(child);

		courseRepository.save(Course.of("test", path));
		courseRepository.save(Course.of("test", child));
		courseRepository.save(Course.of("test", grandChild));

		// 자식으로 조회한다.
		final CourseQueryContainsParentsResult result = courseQueryService.queryParents(grandChild.getPath());

		// 조회된 놈 나오는지 확인
		Assertions.assertEquals(grandChild.getPath(), result.target().coursePath());

		// 부모 나오는지 확인
		Assertions.assertEquals(2, result.parents().size());
	}

	@Test
	void 부모가_없을떈_결과예_포함하지_않는다() {
		final Path path = new Path();
		final Path child = path.nextChild(path);
		final Path grandChild = child.nextChild(child);

		// 부모 제거
		// courseRepository.save(Course.of("test", path));
		// courseRepository.save(Course.of("test", child));
		courseRepository.save(Course.of("test", grandChild));

		// 자식으로 조회한다.
		final CourseQueryContainsParentsResult result = courseQueryService.queryParents(grandChild.getPath());

		// 조회된 놈 나오는지 확인
		Assertions.assertEquals(grandChild.getPath(), result.target().coursePath());

		// 부모 나오는지 확인
		Assertions.assertEquals(0, result.parents().size());
	}

	@Test
	void 결과가_없을땐_에러대신_빈_필드를_가지고_리턴한다() {
		final Path path = new Path();
		final Path child = path.nextChild(path);
		final Path grandChild = child.nextChild(child);

		// 저장 안함 -> DB에 없음

		// courseRepository.save(Course.of("test", path));
		// courseRepository.save(Course.of("test", child));
		// courseRepository.save(Course.of("test", grandChild));

		// 자식으로 조회한다.
		final CourseQueryContainsParentsResult result = courseQueryService.queryParents(grandChild.getPath());

		// 조회된 놈 나오는지 확인
		Assertions.assertNull(result.target().coursePath());
		Assertions.assertNull(result.target().courseName());

		// 부모 나오는지 확인
		Assertions.assertEquals(0, result.parents().size());
	}
}
