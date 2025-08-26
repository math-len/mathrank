package kr.co.mathrank.common.page;

import java.util.List;

public record PageResult<T>(
	List<T> queryResults,
	Integer currentPageNumber,
	Integer currentPageSize,
	List<Integer> possibleNextPageNumbers
) {
	public static <E> PageResult<E> of(
		List<E> queryResults,
		Integer currentPageNumber,
		Integer currentPageSize,
		List<Integer> possibleNextPageNumbers
	) {
		return new PageResult<>(queryResults, currentPageNumber, currentPageSize, possibleNextPageNumbers);
	}
}
