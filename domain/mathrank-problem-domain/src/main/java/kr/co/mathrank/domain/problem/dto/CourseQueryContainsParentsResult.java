package kr.co.mathrank.domain.problem.dto;

import java.util.List;

public record CourseQueryContainsParentsResult(
	CourseQueryResult target,
	List<CourseQueryResult> parents
) {
}
