package kr.co.mathrank.domain.problem.dto;

import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.problem.entity.AnswerType;
import kr.co.mathrank.domain.problem.entity.Difficulty;

public record ProblemQueryCommand(
	Long memberId,
	Difficulty difficultyMinInclude,
	Difficulty difficultyMaxInclude,
	AnswerType answerType,
	String coursePath,
	Boolean videoExist,
	Integer year,
	@NotNull
	@Range(min = 1, max = 20)
	Integer pageSize,
	@NotNull
	@Range(min = 1, max = 1000)
	Integer pageNumber
) {
}
