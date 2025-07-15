package kr.co.mathrank.app.api.problem.read;

import java.util.List;

import kr.co.mathrank.domain.problem.dto.ProblemQueryPageResult;

public record ProblemWithUserNamePageResult(
	List<ProblemWithUserNameResult> queryResults,
	Integer currentPageNumber,
	Integer currentPageSize,
	List<Integer> possibleNextPageNumbers
) {
	public static ProblemWithUserNamePageResult from(
		final List<ProblemWithUserNameResult> userNameResults,
		final ProblemQueryPageResult queryResult
	) {
		return new ProblemWithUserNamePageResult(
			userNameResults,
			queryResult.currentPageNumber(),
			queryResult.currentPageSize(),
			queryResult.possibleNextPageNumbers()
		);
	}
}
