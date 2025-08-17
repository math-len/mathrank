package kr.co.mathrank.app.api.assessment;

import java.time.Duration;
import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kr.co.mathrank.common.role.Role;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentRegisterCommand;

public class Requests {
	record AssessmentRegisterRequest (
		@NotBlank
		String assessmentName,

		@NotNull
		@Size(min = 1)
		List<Long> problemIds,

		@NotNull
		@Min(1)
		@Max(600)
		Long minutes // 시험 시간
	) {
		public AssessmentRegisterCommand toCommand(final Long registerMemberId, final Role role) {
			return new AssessmentRegisterCommand(
				registerMemberId,
				role,
				assessmentName,
				problemIds,
				Duration.ofMinutes(minutes)
			);
		}
	}
}
