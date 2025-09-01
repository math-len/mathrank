package kr.co.mathrank.domain.problem.single.dto;

import java.time.Duration;
import java.util.List;

import jakarta.validation.constraints.NotNull;

public record SingleProblemSolveCommand(
	@NotNull
	Long singleProblemId,
	@NotNull
	Long memberId,
	@NotNull
	List<String> answers,
	@NotNull
	Duration duration
) {
}
