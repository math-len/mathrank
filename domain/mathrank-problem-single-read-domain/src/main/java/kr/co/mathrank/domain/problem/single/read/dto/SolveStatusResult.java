package kr.co.mathrank.domain.problem.single.read.dto;

import kr.co.mathrank.domain.problem.single.read.entity.SingleProblemSolver;

public record SolveStatusResult(
	Long singleProblemId,
	Boolean success
) {
	public static SolveStatusResult from(final SingleProblemSolver singleProblemSolver) {
		return new SolveStatusResult(singleProblemSolver.getSingleProblemReadModel().getProblemId(), singleProblemSolver.isSuccess());
	}
}
