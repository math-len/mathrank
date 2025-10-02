package kr.co.mathrank.domain.problem.assessment.dto;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.role.Role;

public record AssessmentDeleteCommand(
	@NotNull
	Long assessmentId,
	@NotNull
	Long requestMemberId,
	@NotNull
	Role requestMemberRole
) {
}
