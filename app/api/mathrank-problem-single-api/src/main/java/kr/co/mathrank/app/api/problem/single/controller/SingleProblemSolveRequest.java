package kr.co.mathrank.app.api.problem.single.controller;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.problem.single.dto.SingleProblemSolveCommand;

public record SingleProblemSolveRequest (
	@NotNull
	Long singleProblemId,
	@NotNull
	List<String> answers
) {
	public SingleProblemSolveCommand toCommand(final Long memberId) {
		return new SingleProblemSolveCommand(singleProblemId, memberId, answers);
	}
}
