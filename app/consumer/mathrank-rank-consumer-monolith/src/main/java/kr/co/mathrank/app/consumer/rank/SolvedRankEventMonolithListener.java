package kr.co.mathrank.app.consumer.rank;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import kr.co.mathrank.common.event.Event;
import kr.co.mathrank.common.event.publisher.monolith.MonolithEvent;
import kr.co.mathrank.domain.rank.dto.SolverUpdateCommand;
import kr.co.mathrank.domain.rank.service.SolveLogRegisterService;
import kr.co.mathrank.domain.rank.service.SolverUpdateService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class SolvedRankEventMonolithListener {
	private final SolveLogRegisterService solveLogRegisterService;
	private final SolverUpdateService solverUpdateService;

	@EventListener
	@Async
	public void consume(final MonolithEvent monolithEvent) {
		if (!monolithEvent.isExpectedTopic("single-problem-solved")) {
			return;
		}

		final EventPayloads.SingleProblemSolvedEventPayload payload = Event.fromJson(
				monolithEvent.payload(),
				EventPayloads.SingleProblemSolvedEventPayload.class)
			.getPayload();
		solveLogRegisterService.register(payload.toCommand());
	}

	@EventListener
	@Async
	public void consumeMemberUpdatedEvent(final MonolithEvent monolithEvent) {
		if (!monolithEvent.isExpectedTopic("mathrank-member-updated")) {
			return;
		}
		final EventPayloads.MemberUpdatedEventPayload payload = Event.fromJson(
			monolithEvent.payload(),
			EventPayloads.MemberUpdatedEventPayload.class
		).getPayload();

		solverUpdateService.updateSolver(new SolverUpdateCommand(Long.parseLong(payload.memberId()), payload.name(), payload.schoolCode()));
	}
}
