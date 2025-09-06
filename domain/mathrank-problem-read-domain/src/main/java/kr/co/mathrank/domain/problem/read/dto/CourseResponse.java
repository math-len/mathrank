package kr.co.mathrank.domain.problem.read.dto;

import kr.co.mathrank.client.internal.course.CourseQueryResult;

public record CourseResponse(
	String courseName,
	String coursePath
) {
	public static CourseResponse from(CourseQueryResult result) {
		return new CourseResponse(result.courseName(), result.coursePath());
	}
}
