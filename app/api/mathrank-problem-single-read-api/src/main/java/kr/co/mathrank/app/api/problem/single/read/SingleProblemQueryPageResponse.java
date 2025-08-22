package kr.co.mathrank.app.api.problem.single.read;

import java.util.List;

public record SingleProblemQueryPageResponse(
	List<SingleProblemReadModelResponse> queryResults,
	Integer currentPageNumber,
	Integer currentPageSize,
	List<Integer> possibleNextPageNumbers
) {
	public static SingleProblemQueryPageResponse from(
		final List<SingleProblemReadModelResponse> responses,
		final Integer currentPageNumber,
		final Integer currentPageSize,
		final List<Integer> possibleNextPageNumbers
	) {
		return new SingleProblemQueryPageResponse(
			responses,
			currentPageNumber,
			currentPageSize,
			possibleNextPageNumbers
		);
	}
}
