package kr.co.mathrank.domain.problem.assessment.dto;

import java.time.Duration;
import java.util.List;

import jakarta.validation.constraints.NotNull;

public record SubmissionRegisterCommand(
	@NotNull
	Long memberId,
	@NotNull
	Long assessmentId,
	@NotNull
	List<List<String>> submittedAnswers,
	@NotNull
	Duration elapsedTime
) {
}
