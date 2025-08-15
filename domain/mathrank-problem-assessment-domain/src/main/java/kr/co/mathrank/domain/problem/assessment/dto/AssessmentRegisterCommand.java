package kr.co.mathrank.domain.problem.assessment.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kr.co.mathrank.common.role.Role;

public record AssessmentRegisterCommand(
	@NotNull
	Long registerMemberId,

	@NotNull
	Role role,

	@NotBlank
	String assessmentName,

	@NotNull
	@Size(min = 1)
	List<Long> problemIds
) {
}
