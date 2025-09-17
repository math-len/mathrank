package kr.co.mathrank.domain.problem.single.read.service;

import static kr.co.mathrank.domain.problem.single.read.SingleProblemReadModelConfiguration.*;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.client.internal.course.CourseClient;
import kr.co.mathrank.client.internal.course.CourseQueryContainsParentsResult;
import kr.co.mathrank.domain.problem.single.read.dto.CourseContainsParentResult;
import lombok.RequiredArgsConstructor;

@Component
@CacheConfig(cacheNames = COURSE_CACHE)
@RequiredArgsConstructor
class CourseManager {
	private final CourseClient courseClient;

	@Cacheable(key = "#coursePath")
	public CourseContainsParentResult courseQueryResult(@NotNull final String coursePath) {
		final CourseQueryContainsParentsResult result = courseClient.getParentCourses(coursePath);
		return result == null ? CourseContainsParentResult.none() : CourseContainsParentResult.from(result);
	}
}
