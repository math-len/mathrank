package kr.co.mathrank.domain.problem.single.dto;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.role.Role;

public record SingleProblemRegisterCommand(
	@NotNull
	Long problemId,
	@NotNull
	Long memberId,
	@NotNull
	Role role
) {
}
