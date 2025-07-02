package kr.co.mathrank.domain.problem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.problem.entity.AnswerType;
import kr.co.mathrank.domain.problem.entity.Difficulty;
import kr.co.mathrank.domain.problem.entity.ProblemCourse;

public record ProblemRegisterCommand(
	@NotNull
	Long requestMemberId,
	@NotNull
	String imageSource,
	@NotNull
	AnswerType answerType,
	@NotNull
	Difficulty difficulty,
	@NotNull
	String schoolCode,
	@NotBlank
	String answer,
	@NotNull
	ProblemCourse problemCourse
) {
}
