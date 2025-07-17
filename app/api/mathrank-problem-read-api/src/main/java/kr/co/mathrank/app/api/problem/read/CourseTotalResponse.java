package kr.co.mathrank.app.api.problem.read;

import java.util.List;

import kr.co.mathrank.domain.problem.dto.CourseQueryContainsParentsResult;

public record CourseTotalResponse(
	CourseResponse target,
	List<CourseResponse> parents
) {
	public static CourseTotalResponse from(CourseQueryContainsParentsResult result) {
		return new CourseTotalResponse(
			CourseResponse.from(result.target()),
			result.parents().stream()
				.map(CourseResponse::from)
				.toList());
	}
}
