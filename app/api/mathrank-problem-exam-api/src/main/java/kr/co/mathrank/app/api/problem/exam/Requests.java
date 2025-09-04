package kr.co.mathrank.app.api.problem.exam;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kr.co.mathrank.common.role.Role;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentItemRegisterCommand;
import kr.co.mathrank.domain.problem.assessment.dto.LimitedAssessmentRegisterCommand;
import kr.co.mathrank.domain.problem.assessment.dto.SubmissionRegisterCommand;

public class Requests {
	record ExamRegisterRequest(
		@NotBlank
		String examName,

		@NotNull
		@Size(min = 1)
		@Valid
		List<ExamItemRegisterRequest> items,

		@NotNull
		@Min(1)
		@Max(600)
		Long minutes, // 시험 시간

		@NotNull
		LocalDateTime startAt,
		@NotNull
		LocalDateTime endAt
	) {
		public LimitedAssessmentRegisterCommand toCommand(final Long registerMemberId, final Role role) {
			return new LimitedAssessmentRegisterCommand(
				registerMemberId,
				role,
				examName,
				items.stream()
					.map(ExamItemRegisterRequest::toCommand)
					.toList(),
				Duration.ofMinutes(minutes),
				startAt,
				endAt
			);
		}
	}

	record ExamItemRegisterRequest(
		@NotNull
		Long problemId,
		@NotNull
		Integer score
	) {
		AssessmentItemRegisterCommand toCommand() {
			return new AssessmentItemRegisterCommand(problemId, score);
		}
	}

	record ExamSubmissionRegisterRequest(
		@NotNull
		Long examId,
		@NotNull
		List<List<String>> submittedAnswers,
		@NotNull
		Long elapsedTimeSeconds
	) {
		public SubmissionRegisterCommand toCommand(final Long memberId) {
			return new SubmissionRegisterCommand(memberId, examId, submittedAnswers,
				Duration.ofSeconds(elapsedTimeSeconds));
		}
	}
}
