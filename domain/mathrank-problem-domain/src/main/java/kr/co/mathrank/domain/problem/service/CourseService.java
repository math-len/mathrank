package kr.co.mathrank.domain.problem.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.problem.dto.CourseRegisterCommand;
import kr.co.mathrank.domain.problem.entity.Course;
import kr.co.mathrank.domain.problem.entity.Path;
import kr.co.mathrank.domain.problem.exception.CannotFoundCourseException;
import kr.co.mathrank.domain.problem.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class CourseService {
	private final CourseRepository courseRepository;

	@Transactional
	public String register(@NotNull @Valid final CourseRegisterCommand command) {
		Path parentPath;
		try {
			parentPath = new Path(command.parentPath());
		} catch (IllegalStateException e) {
			log.warn("[CourseService.register] cannot create path with: {}", command.parentPath());
			throw new CannotFoundCourseException(command.parentPath());
		}

		final Course longestPath = getLatestPathCourse(parentPath);

		// 첫 삽입일 경우
		if (command.parentPath().isBlank() && longestPath == null) {
			return courseRepository.save(Course.of(command.courseName(), new Path())).getPath().getPath();
		}

		// 존재하지 않는 부모 조회
		if (!command.parentPath().isBlank() && longestPath == null) {
			log.warn("[CourseService.register] cannot create path with: {}", command.parentPath());
			throw new CannotFoundCourseException(command.parentPath());
		}

		final Path childPath = parentPath.nextChild(longestPath.getPath());

		final Course course = Course.of(command.courseName(), childPath);
		final String resultPath = courseRepository.save(course).getPath().getPath();

		log.info("[CourseService.register] course saved - courseName: {}, coursePath: {}", course.getCourseName(), course.getPath());
		return resultPath;
	}

	private Course getLatestPathCourse(final Path path) {
		return courseRepository.queryCourseStartsWith(path.getPath(),
				PageRequest.ofSize(1).withSort(Sort.by(Sort.Direction.DESC, "path")))
			.stream()
			.findFirst()
			.orElse(null);
	}
}
