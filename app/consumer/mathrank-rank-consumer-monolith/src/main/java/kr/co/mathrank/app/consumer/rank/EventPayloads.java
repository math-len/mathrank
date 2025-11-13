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

	record MemberUpdatedEventPayload(
		String memberId,
		String name,
		String schoolCode
	) implements EventPayload {
	}

	record MemberDeletedEventPayload(
		Long memberId,
		String memberName
	) implements EventPayload {
	}
}
