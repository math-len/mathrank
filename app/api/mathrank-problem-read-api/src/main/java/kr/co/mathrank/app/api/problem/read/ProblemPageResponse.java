package kr.co.mathrank.app.api.problem.read;

import java.util.List;

import kr.co.mathrank.domain.problem.dto.ProblemQueryPageResult;

public record ProblemPageResponse(
	List<ProblemResponse> queryResults,
	Integer currentPageNumber,
	Integer currentPageSize,
	List<Integer> possibleNextPageNumbers
) {
	public static ProblemPageResponse from(
		final List<ProblemResponse> userNameResults,
		final ProblemQueryPageResult queryResult
	) {
		return new ProblemPageResponse(
			userNameResults,
			queryResult.currentPageNumber(),
			queryResult.currentPageSize(),
			queryResult.possibleNextPageNumbers()
		);
	}
}
