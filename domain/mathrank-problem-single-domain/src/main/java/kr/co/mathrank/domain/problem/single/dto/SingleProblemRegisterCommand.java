package kr.co.mathrank.domain.problem.single.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.role.Role;

public record SingleProblemRegisterCommand(
	@NotNull
	Long problemId,
	@NotBlank
	String singleProblemName,
	@NotNull
	Long memberId,
	@NotNull
	Role role
) {
}
