package kr.co.mathrank.domain.problem.single.read.dto;

import kr.co.mathrank.client.internal.course.CourseQueryResult;

public record CourseInfoResult(
	String coursePath,
	String courseName
) {
	public static CourseInfoResult from(final CourseQueryResult courseQueryResult) {
		return new CourseInfoResult(courseQueryResult.coursePath(), courseQueryResult.courseName());
	}
}
