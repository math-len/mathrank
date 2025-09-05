package kr.co.mathrank.domain.problem.dto;

import kr.co.mathrank.domain.problem.core.AnswerType;
import kr.co.mathrank.domain.problem.core.Difficulty;

public record ProblemQuery(
	Long memberId,
	Long problemId,
	Difficulty difficultyMinInclude,
	Difficulty difficultyMaxInclude,
	AnswerType answerType,
	String path,
	Boolean solutionVideoExist,
	Integer year,
	String location
) {
}
