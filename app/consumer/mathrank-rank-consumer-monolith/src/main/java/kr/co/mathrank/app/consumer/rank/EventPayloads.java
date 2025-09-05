package kr.co.mathrank.app.consumer.rank;

import kr.co.mathrank.common.event.EventPayload;
import kr.co.mathrank.domain.rank.dto.SolveLogRegisterCommand;

public class EventPayloads {
	record SingleProblemSolvedEventPayload(
		Long singleProblemId,
		Long problemId,
		Long memberId,
		Boolean success,
		Long firstTrySuccessCount,
		Long totalAttemptedCount,
		Long attemptedUserDistinctCount
	) implements EventPayload {
		SolveLogRegisterCommand toCommand() {
			return new SolveLogRegisterCommand(
				singleProblemId,
				problemId,
				memberId,
				success
			);
		}
	}
}
