package kr.co.mathrank.domain.problem.single.read.dto;

import kr.co.mathrank.domain.problem.core.AnswerType;
import kr.co.mathrank.domain.problem.core.Difficulty;

public record SingleProblemReadModelQuery(
	Long singleProblemId,

	String singleProblemName,

	String coursePath,

	AnswerType answerType,

	Difficulty difficultyMinInclude,
	Difficulty difficultyMaxInclude,

	Integer accuracyMinInclude,
	Integer accuracyMaxInclude,

	Long totalAttemptCountMinInclude,
	Long totalAttemptCountMaxInclude
) {
}
