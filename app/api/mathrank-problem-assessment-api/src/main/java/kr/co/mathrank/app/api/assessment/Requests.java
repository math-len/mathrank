package kr.co.mathrank.app.api.assessment;

import java.time.Duration;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kr.co.mathrank.common.role.Role;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentItemRegisterCommand;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentRegisterCommand;
import kr.co.mathrank.domain.problem.assessment.dto.SubmissionRegisterCommand;

public class Requests {
	record AssessmentRegisterRequest (
		@NotBlank
		String assessmentName,

		@NotNull
		@Size(min = 1)
		@Valid
		List<AssessmentItemRegisterRequest> items,

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
				items.stream()
					.map(AssessmentItemRegisterRequest::toCommand)
					.toList(),
				Duration.ofMinutes(minutes)
			);
		}
	}

	record AssessmentItemRegisterRequest(
		@NotNull
		Long problemId,
		@NotNull
		Integer score
	) {
		AssessmentItemRegisterCommand toCommand() {
			return new AssessmentItemRegisterCommand(problemId, score);
		}
	}

	record AssessmentSubmissionRegisterRequest(
		@NotNull
		Long assessmentId,
		@NotNull
		List<List<String>> submittedAnswers,
		@NotNull
		Duration elapsedTime
	) {
		public SubmissionRegisterCommand toCommand(final Long memberId) {
			return new SubmissionRegisterCommand(memberId, assessmentId, submittedAnswers, elapsedTime);
		}
	}
}
