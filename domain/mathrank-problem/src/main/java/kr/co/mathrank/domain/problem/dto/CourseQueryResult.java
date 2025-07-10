package kr.co.mathrank.domain.problem.dto;

import kr.co.mathrank.domain.problem.entity.Course;

public record CourseQueryResult(
	String coursePath,
	String courseName
) {
	public static CourseQueryResult from(Course course) {
		return new CourseQueryResult(course.getPath().getPath(), course.getCourseName());
	}
}
