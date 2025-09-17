package kr.co.mathrank.domain.problem.single.read.dto;

import java.util.List;

import kr.co.mathrank.client.internal.course.CourseQueryContainsParentsResult;

public record CourseContainsParentResult(
	CourseInfoResult target,
	List<CourseInfoResult> parents
) {
	public static CourseContainsParentResult from(CourseQueryContainsParentsResult result) {
		return new CourseContainsParentResult(
			CourseInfoResult.from(result.target()),
			result.parents().stream()
				.map(CourseInfoResult::from)
				.toList()
		);
	}

	public static CourseContainsParentResult none() {
		return new CourseContainsParentResult(null, null);
	}
}
