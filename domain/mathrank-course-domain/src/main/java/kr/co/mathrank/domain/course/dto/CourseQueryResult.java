package kr.co.mathrank.domain.course.dto;

import kr.co.mathrank.domain.course.entity.Course;

public record CourseQueryResult(
	String coursePath,
	String courseName
) {
	public static CourseQueryResult from(Course course) {
		return new CourseQueryResult(course.getPath().getPath(), course.getCourseName());
	}

	public static CourseQueryResult none() {
		return new CourseQueryResult(null, null);
	}
}
