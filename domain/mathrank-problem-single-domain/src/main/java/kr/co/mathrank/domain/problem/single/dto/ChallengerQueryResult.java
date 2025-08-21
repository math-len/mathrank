package kr.co.mathrank.domain.problem.single.dto;

import kr.co.mathrank.domain.problem.single.entity.Challenger;

public record ChallengerQueryResult(
	Long singleProblemId,
	Boolean success
) {
	public static ChallengerQueryResult from(final Challenger challenger) {
		return new ChallengerQueryResult(challenger.getSingleProblem().getProblemId(),
			challenger.isSuccessAtFirstTry());
	}
}
