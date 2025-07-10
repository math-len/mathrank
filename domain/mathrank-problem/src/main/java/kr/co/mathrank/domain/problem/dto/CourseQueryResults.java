package kr.co.mathrank.domain.problem.dto;

import java.util.List;

public record CourseQueryResults(
	List<CourseQueryResult> results
) {
}
