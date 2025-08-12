package kr.co.mathrank.app.api.problem.single.read;

import kr.co.mathrank.domain.problem.core.AnswerType;
import kr.co.mathrank.domain.problem.core.Difficulty;
import kr.co.mathrank.domain.problem.single.read.dto.SingleProblemReadModelQuery;

public record SingleProblemQueryRequest(
	Long singleProblemId,

	String coursePath,

	AnswerType answerType,

	Difficulty difficultyMinInclude,
	Difficulty difficultyMaxInclude,

	Integer accuracyMinInclude,
	Integer accuracyMaxInclude,

	Long totalAttemptCountMinInclude,
	Long totalAttemptCountMaxInclude
) {
	public SingleProblemReadModelQuery toQuery() {
		return new SingleProblemReadModelQuery(
			singleProblemId,
			coursePath,
			answerType,
			difficultyMinInclude,
			difficultyMaxInclude,
			accuracyMinInclude,
			accuracyMaxInclude,
			totalAttemptCountMinInclude,
			totalAttemptCountMaxInclude);
	}
}
