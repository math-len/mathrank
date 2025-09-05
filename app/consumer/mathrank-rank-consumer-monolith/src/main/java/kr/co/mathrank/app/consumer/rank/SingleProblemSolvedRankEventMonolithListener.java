package kr.co.mathrank.app.consumer.rank;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import kr.co.mathrank.common.event.Event;
import kr.co.mathrank.common.event.publisher.monolith.MonolithEvent;
import kr.co.mathrank.domain.rank.service.SolveLogRegisterService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class SingleProblemSolvedRankEventMonolithListener {
	private final SolveLogRegisterService solveLogRegisterService;

	@EventListener
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
}
