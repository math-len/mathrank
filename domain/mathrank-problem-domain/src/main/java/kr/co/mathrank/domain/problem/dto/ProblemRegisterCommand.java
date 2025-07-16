package kr.co.mathrank.domain.problem.dto;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
	String schoolCode,
	@Size(min = 1, max = 100)
	Set<String> answers,
	Integer year,
	String solutionVideoLink
) {
}
