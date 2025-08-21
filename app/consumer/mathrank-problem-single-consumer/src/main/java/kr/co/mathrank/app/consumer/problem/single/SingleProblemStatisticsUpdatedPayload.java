package kr.co.mathrank.app.consumer.problem.single;

import kr.co.mathrank.common.event.EventPayload;
import kr.co.mathrank.domain.problem.single.read.dto.SingleProblemAttemptStatsUpdateCommand;

public record SingleProblemStatisticsUpdatedPayload(
	Long singleProblemId,
	Long problemId,
	Long memberId,
	Boolean success,
	Long firstTrySuccessCount,
	Long totalAttemptedCount,
	Long attemptedUserDistinctCount
) implements EventPayload {
	public SingleProblemAttemptStatsUpdateCommand toCommand() {
		return new SingleProblemAttemptStatsUpdateCommand(
			this.singleProblemId,
			memberId,
			success,
			this.firstTrySuccessCount,
			this.totalAttemptedCount,
			this.attemptedUserDistinctCount
		);
	}
}
