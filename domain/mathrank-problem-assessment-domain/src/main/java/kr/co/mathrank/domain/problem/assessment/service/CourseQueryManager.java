package kr.co.mathrank.domain.problem.assessment.service;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.client.internal.course.CourseClient;
import kr.co.mathrank.client.internal.course.CourseQueryResult;
import kr.co.mathrank.domain.problem.assessment.AssessmentReadDomainConfiguration;
import lombok.RequiredArgsConstructor;

@Component
@Validated
@RequiredArgsConstructor
@CacheConfig(cacheNames = AssessmentReadDomainConfiguration.COURSE_CACHE_NAME)
class CourseQueryManager {
	private final CourseClient courseClient;

	@Cacheable(key = "#coursePath")
	public CourseQueryResult getCourseInfo(@NotNull final String coursePath) {
		return courseClient.getParentCourses(coursePath).target();
	}
}
