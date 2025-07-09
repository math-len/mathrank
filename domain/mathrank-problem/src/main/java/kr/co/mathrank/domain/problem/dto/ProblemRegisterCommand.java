package kr.co.mathrank.domain.problem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.problem.entity.AnswerType;
import kr.co.mathrank.domain.problem.entity.Difficulty;

public record ProblemRegisterCommand(
	@NotNull
	Long requestMemberId,
	@NotNull
	String imageSource,
	@NotBlank
	String solutionImage,
	@NotNull
	AnswerType answerType,
	@NotNull
	String coursePath,
	@NotNull
	Difficulty difficulty,
	@NotNull
	String schoolCode,
	@NotBlank
	String answer,
	String solutionVideoLink
) {
}
