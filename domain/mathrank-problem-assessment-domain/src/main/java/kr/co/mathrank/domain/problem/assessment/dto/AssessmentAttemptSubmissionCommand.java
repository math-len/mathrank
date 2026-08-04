package kr.co.mathrank.domain.problem.assessment.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;

public record AssessmentAttemptSubmissionCommand(
	@NotNull
	Long memberId,

	@NotNull
	Long attemptId,

	@NotNull
	List<List<String>> submittedAnswers
) {
}
