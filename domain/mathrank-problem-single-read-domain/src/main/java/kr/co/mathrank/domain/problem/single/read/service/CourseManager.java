package kr.co.mathrank.domain.problem.single.read.service;

import org.springframework.stereotype.Component;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.client.internal.course.CourseClient;
import kr.co.mathrank.client.internal.course.CourseQueryContainsParentsResult;
import kr.co.mathrank.domain.problem.single.read.dto.CourseContainsParentResult;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class CourseManager {
	private final CourseClient courseClient;

	public CourseContainsParentResult courseQueryResult(@NotNull final String coursePath) {
		final CourseQueryContainsParentsResult result = courseClient.getParentCourses(coursePath);
		return result == null ? CourseContainsParentResult.none() : CourseContainsParentResult.from(result);
	}
}
