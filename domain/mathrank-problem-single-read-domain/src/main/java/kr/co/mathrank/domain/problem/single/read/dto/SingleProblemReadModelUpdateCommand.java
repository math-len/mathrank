package kr.co.mathrank.domain.problem.single.read.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.problem.core.AnswerType;
import kr.co.mathrank.domain.problem.core.Difficulty;
import kr.co.mathrank.domain.problem.core.PastProblem;

public record SingleProblemReadModelUpdateCommand(
	@NotNull
	Long problemId, // problemId is different from singleProblemId
	@NotNull
	String coursePath,
	@NotNull
	String problemImage,
	String location,
	String schoolCode,
	@NotNull
	AnswerType answerType,
	@NotNull
	Difficulty difficulty,
	PastProblem pastProblem,
	@NotNull
	LocalDateTime updatedAt
) {
}
