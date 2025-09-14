package kr.co.mathrank.app.api.problem.single.controller;

import java.util.List;
import java.util.Set;

import kr.co.mathrank.domain.problem.single.dto.SingleProblemSolveResult;

public record SingleProblemSolveResponse(
	String challengeLogId,
	Boolean success,
	Set<String> realAnswer,
	List<String> submittedAnswer
) {
	public static SingleProblemSolveResponse from(final SingleProblemSolveResult result) {
		return new SingleProblemSolveResponse(
			String.valueOf(result.challengeLogId()),
			result.success(),
			result.realAnswer(),
			result.submittedAnswer()
		);
	}
}
