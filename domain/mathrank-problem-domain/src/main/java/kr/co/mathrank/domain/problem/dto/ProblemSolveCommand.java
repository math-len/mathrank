package kr.co.mathrank.domain.problem.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;

public record ProblemSolveCommand(
	@NotNull
	Long problemId,
	@NotNull
	List<String> submittedAnswer
) {
}
