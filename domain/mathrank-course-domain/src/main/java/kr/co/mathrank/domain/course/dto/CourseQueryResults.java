package kr.co.mathrank.domain.course.dto;

import java.util.List;

public record CourseQueryResults(
	List<CourseQueryResult> results
) {
}
