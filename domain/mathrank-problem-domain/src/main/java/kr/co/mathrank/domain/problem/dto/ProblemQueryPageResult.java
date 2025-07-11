package kr.co.mathrank.domain.problem.dto;

import static java.util.stream.Collectors.*;

import java.util.List;

import kr.co.mathrank.domain.problem.entity.Problem;

public record ProblemQueryPageResult(
	List<ProblemQueryResult> queryResults,
	Integer currentPageNumber,
	Integer currentPageSize,
	List<Integer> possibleNextPageNumbers
) {
	public static ProblemQueryPageResult of(List<Problem> problems, int currentPageNumber, int currentPageSize, List<Integer> possibleNextPageNumbers) {
		return new ProblemQueryPageResult(
			problems.stream()
				.map(ProblemQueryResult::from)
				.collect(toList()),
			currentPageNumber,
			currentPageSize,
			possibleNextPageNumbers
		);
	}
}
