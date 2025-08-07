package kr.co.mathrank.domain.problem.single.read.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.problem.core.AnswerType;
import kr.co.mathrank.domain.problem.core.Difficulty;

public record SingleProblemReadModelRegisterCommand(
	@NotNull
	Long singleProblemId,
	@NotNull
	Long problemId,
	@NotNull
	String problemImage,
	@NotNull
	AnswerType answerType,
	@NotNull
	Difficulty difficulty,
	@NotNull
	String coursePath,
	@NotNull
	LocalDateTime createdAt
) {
}
