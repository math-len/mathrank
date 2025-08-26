package kr.co.mathrank.domain.problem.assessment.read.dto;

import kr.co.mathrank.client.internal.course.CourseQueryResult;

public record CourseDetailResult(
	String coursePath,
	String courseName
) {
	public static CourseDetailResult from(CourseQueryResult courseQueryResult) {
		return new CourseDetailResult(courseQueryResult.coursePath(), courseQueryResult.courseName());
	}

}
