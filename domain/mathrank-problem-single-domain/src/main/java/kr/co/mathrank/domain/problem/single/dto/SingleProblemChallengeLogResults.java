package kr.co.mathrank.domain.problem.single.dto;

import java.util.List;

import kr.co.mathrank.domain.problem.single.entity.ChallengeLog;

public record SingleProblemChallengeLogResults(
	List<SingleProblemChallengeLogResult> results
) {
	public static SingleProblemChallengeLogResults from(final List<ChallengeLog> challengeLogs) {
		return new SingleProblemChallengeLogResults(challengeLogs.stream()
			.map(SingleProblemChallengeLogResult::from)
			.toList());
	}
}
