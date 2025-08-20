package kr.co.mathrank.app.api.problem.single.controller;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.role.Role;
import kr.co.mathrank.domain.problem.single.dto.SingleProblemRegisterCommand;

public record SingleProblemRegisterRequest(
	@NotNull
	Long problemId,
	@NotNull
	String singleProblemName
) {
	public SingleProblemRegisterCommand toCommand(final Long memberId, final Role role) {
		return new SingleProblemRegisterCommand(problemId, singleProblemName, memberId, role);
	}
}
