package kr.co.mathrank.domain.problem.assessment.dto;

import java.time.Duration;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kr.co.mathrank.common.role.Role;
import kr.co.mathrank.domain.problem.assessment.constraint.AssessmentDurationConstraint;
import kr.co.mathrank.domain.problem.assessment.constraint.ScoreSum;
import kr.co.mathrank.domain.problem.core.Difficulty;

public record AssessmentRegisterCommand(
	@NotNull
	Long registerMemberId,

	@NotNull
	Role role,

	@NotBlank
	String assessmentName,

	@NotNull
	@Size(min = 1)
	@ScoreSum
	@Valid
	List<AssessmentItemRegisterCommand> assessmentItems,

	@NotNull
	Difficulty difficulty,

	@NotNull
	@AssessmentDurationConstraint(minIncludeMinutes = 1, maxIncludeMinutes = 60 * 10)
	Duration minutes // 시험 시간
) {
}
