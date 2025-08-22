package kr.co.mathrank.domain.course.dto;

import java.util.List;

public record CourseQueryContainsParentsResult(
	CourseQueryResult target,
	List<CourseQueryResult> parents
) {
}
