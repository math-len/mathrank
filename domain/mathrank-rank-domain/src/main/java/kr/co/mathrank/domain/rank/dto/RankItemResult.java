package kr.co.mathrank.domain.rank.dto;

import kr.co.mathrank.domain.rank.entity.SolveLog;
import kr.co.mathrank.domain.rank.entity.Solver;
import kr.co.mathrank.domain.rank.entity.Tier;

public record RankItemResult(
	Long memberId,
	Long rank,
	Tier tier,
	Long score,
	Long totalSubmittedCount,
	Long successCount
) {
	public static RankItemResult from(
		final Solver solver,
		final Long rank,
		final Tier tier
	) {
		return new RankItemResult(
			solver.getMemberId(),
			rank,
			tier,
			solver.getScore(),
			solver.getSolveLogs().stream().count(),
			solver.getSolveLogs().stream()
				.filter(SolveLog::isSuccess)
				.count()
		);
	}
}
