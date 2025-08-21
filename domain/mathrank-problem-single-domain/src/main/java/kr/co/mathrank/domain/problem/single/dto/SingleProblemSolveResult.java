package kr.co.mathrank.domain.problem.single.dto;

import java.util.List;
import java.util.Set;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.client.internal.problem.SolveResult;

public record SingleProblemSolveResult(
	@NotNull
	Boolean success,
	@NotNull
	Set<String> realAnswer,
	@NotNull
	List<String> submittedAnswer
) {
	public static SingleProblemSolveResult from(SolveResult solveResult) {
		return new SingleProblemSolveResult(
			solveResult.success(),
			solveResult.realAnswer(),
			solveResult.submittedAnswer()
		);
	}
}
