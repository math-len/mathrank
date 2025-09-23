package kr.co.mathrank.domain.problem.single.read.dto;

import kr.co.mathrank.domain.problem.core.AnswerType;
import kr.co.mathrank.domain.problem.core.Difficulty;
import kr.co.mathrank.domain.problem.core.PastProblem;

public record SingleProblemReadModelQuery(
	Long singleProblemId,

	String singleProblemName,

	String coursePath,

	String location,

	String schoolCode,

	AnswerType answerType,

	PastProblem pastProblem,

	Difficulty difficultyMinInclude,
	Difficulty difficultyMaxInclude,

	Integer accuracyMinInclude,
	Integer accuracyMaxInclude,

	Long totalAttemptCountMinInclude,
	Long totalAttemptCountMaxInclude
) {
}
