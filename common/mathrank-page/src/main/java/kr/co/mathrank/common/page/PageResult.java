package kr.co.mathrank.common.page;

import java.util.List;
import java.util.function.Function;

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

	public <U> PageResult<U> map(Function<? super T, U> function) {
		return PageResult.of(
			this.queryResults.stream()
				.map(function)
				.toList(),
			this.currentPageNumber,
			this.currentPageSize,
			this.possibleNextPageNumbers
		);
	}
}
