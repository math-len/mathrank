package kr.co.mathrank.domain.problem.dto;

import kr.co.mathrank.domain.problem.core.AnswerType;
import kr.co.mathrank.domain.problem.core.Difficulty;
import kr.co.mathrank.domain.problem.core.PastProblem;

public record ProblemQuery(
	Long memberId,
	Long problemId,
	Difficulty difficultyMinInclude,
	Difficulty difficultyMaxInclude,
	AnswerType answerType,
	String path,
	Boolean solutionVideoExist,
	PastProblem pastProblem,
	Integer year,
	String location,
	String schoolCode
) {
}
