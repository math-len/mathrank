package kr.co.mathrank.domain.problem.assessment.dto;

import java.time.Duration;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kr.co.mathrank.common.role.Role;
import kr.co.mathrank.domain.problem.assessment.constraint.AssessmentDurationConstraint;

public record AssessmentRegisterCommand(
	@NotNull
	Long registerMemberId,

	@NotNull
	Role role,

	@NotBlank
	String assessmentName,

	@NotNull
	@Size(min = 1)
	List<AssessmentItemRegisterCommand> assessmentItems,

	@NotNull
	@AssessmentDurationConstraint(minIncludeMinutes = 1, maxIncludeMinutes = 60 * 10)
	Duration minutes // 시험 시간
) {
}
