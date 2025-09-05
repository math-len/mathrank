package kr.co.mathrank.domain.problem.single.read.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.problem.core.AnswerType;
import kr.co.mathrank.domain.problem.core.Difficulty;

public record SingleProblemReadModelUpdateCommand(
	@NotNull
	Long problemId, // problemId is different from singleProblemId
	@NotNull
	String coursePath,
	@NotNull
	String problemImage,
	@NotNull
	String location,
	@NotNull
	AnswerType answerType,
	@NotNull
	Difficulty difficulty,
	@NotNull
	LocalDateTime updatedAt
) {
}
