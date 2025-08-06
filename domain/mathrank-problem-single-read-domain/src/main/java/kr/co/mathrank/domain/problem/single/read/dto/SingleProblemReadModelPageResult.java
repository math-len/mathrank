package kr.co.mathrank.domain.problem.single.read.dto;

import java.util.List;

public record SingleProblemReadModelPageResult(
	List<SingleProblemReadModelResult> queryResults,
	Integer currentPageNumber,
	Integer currentPageSize,
	List<Integer> possibleNextPageNumbers
) {
}
