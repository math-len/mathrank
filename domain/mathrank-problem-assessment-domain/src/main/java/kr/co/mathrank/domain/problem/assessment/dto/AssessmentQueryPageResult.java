package kr.co.mathrank.domain.problem.assessment.dto;

import java.util.List;

import kr.co.mathrank.domain.problem.assessment.entity.Assessment;

public record AssessmentQueryPageResult(
	List<AssessmentQueryResult> queryResults,
	Integer currentPageNumber,
	Integer currentPageSize,
	List<Integer> possibleNextPageNumbers
) {
	public static AssessmentQueryPageResult from(final List<Assessment> assessments, final int pageNumber, final int pageSize, final List<Integer> possibleNextPageNumbers) {
		return new AssessmentQueryPageResult(
			assessments.stream()
				.map(AssessmentQueryResult::from)
				.toList(),
			pageNumber,
			pageSize,
			possibleNextPageNumbers
		);
	}
}
