package kr.co.mathrank.app.api.problem.read;

import kr.co.mathrank.domain.problem.dto.CourseQueryResult;

public record CourseResponse(
	String courseName,
	String coursePath
) {
	public static CourseResponse from(CourseQueryResult result) {
		return new CourseResponse(result.courseName(), result.coursePath());
	}
}
