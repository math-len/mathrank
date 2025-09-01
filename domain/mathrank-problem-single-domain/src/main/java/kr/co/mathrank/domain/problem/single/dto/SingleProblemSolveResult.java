package kr.co.mathrank.domain.problem.single.dto;

import java.util.List;
import java.util.Set;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.client.internal.problem.SolveResult;

public record SingleProblemSolveResult(
	Long challengeLogId,
	@NotNull
	Boolean success,
	@NotNull
	Set<String> realAnswer,
	@NotNull
	List<String> submittedAnswer
) {
	public static SingleProblemSolveResult from(SolveResult solveResult) {
		return new SingleProblemSolveResult(
			null,
			solveResult.success(),
			solveResult.correctAnswer(),
			solveResult.submittedAnswer()
		);
	}

	public static SingleProblemSolveResult from(SolveResult solveResult, Long challengeLogId) {
		return new SingleProblemSolveResult(
			challengeLogId,
			solveResult.success(),
			solveResult.correctAnswer(),
			solveResult.submittedAnswer()
		);
	}
}
