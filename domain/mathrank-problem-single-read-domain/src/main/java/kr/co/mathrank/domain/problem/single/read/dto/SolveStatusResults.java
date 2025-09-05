package kr.co.mathrank.domain.problem.single.read.dto;

import java.util.List;

import kr.co.mathrank.domain.problem.single.read.entity.SingleProblemSolver;

public record SolveStatusResults(
	List<SolveStatusResult> solveStatusResults
) {
	public static SolveStatusResults from(final List<SingleProblemSolver> solvers) {
		return new SolveStatusResults(solvers.stream()
			.map(SolveStatusResult::from)
			.toList());
	}
}
