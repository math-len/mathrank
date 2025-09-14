package kr.co.mathrank.app.api.problem.single.read;

import java.util.List;

import kr.co.mathrank.domain.problem.single.read.dto.SolveStatusResult;
import kr.co.mathrank.domain.problem.single.read.dto.SolveStatusResults;

public record SolveStatusResponse(
	List<String> solvedSingleProblemIds,
	List<String> failedSingleProblemIds
) {
	public static SolveStatusResponse from(final SolveStatusResults solveStatusResults) {
		return new SolveStatusResponse(
			solveStatusResults.solveStatusResults().stream()
				.filter(SolveStatusResult::success)
				.map(SolveStatusResult::singleProblemId)
				.map(String::valueOf)
				.toList(),
			solveStatusResults.solveStatusResults().stream()
				.filter(solveStatusResult -> !solveStatusResult.success())
				.map(SolveStatusResult::singleProblemId)
				.map(String::valueOf)
				.toList()
		);
	}
}
