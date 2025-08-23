package kr.co.mathrank.client.internal.course;

import java.util.List;

public record CourseQueryContainsParentsResult(
	CourseQueryResult target,
	List<CourseQueryResult> parents
) {
}
