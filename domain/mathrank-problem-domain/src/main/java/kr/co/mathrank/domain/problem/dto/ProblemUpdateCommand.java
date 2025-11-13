package kr.co.mathrank.domain.problem.dto;

import java.util.Set;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kr.co.mathrank.domain.problem.core.AnswerType;
import kr.co.mathrank.domain.problem.core.Difficulty;
import kr.co.mathrank.domain.problem.core.PastProblem;

public record ProblemUpdateCommand(
	@NotNull
	Long problemId,
	@NotNull
	Long requestMemberId,
	@NotNull
	String imageSource,
	/*
		문제 수정시, 풀이 이미지 필수 아니도록 수정
	*/
	// @NotNull
	String solutionImage,
	@NotNull
	AnswerType answerType,
	@NotNull
	Difficulty difficulty,
	@NotNull
	PastProblem pastProblem,
	@NotNull
	String coursePath,
	String schoolCode,
	@Size(min = 1, max = 100)
	Set<String> answers,
	Integer year,
	String solutionVideoLink,
	String memo
) {
}
