package kr.co.mathrank.app.consumer.problem.single.read.consumer.monolith;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import kr.co.mathrank.app.consumer.problem.single.read.consumer.monolith.EventPayloads.ProblemUpdatedEventPayload;
import kr.co.mathrank.app.consumer.problem.single.read.consumer.monolith.EventPayloads.SingleProblemRegisteredEventPayload;
import kr.co.mathrank.app.consumer.problem.single.read.consumer.monolith.EventPayloads.SingleProblemSolvedEventPayload;
import kr.co.mathrank.common.event.Event;
import kr.co.mathrank.common.event.publisher.monolith.MonolithEvent;
import kr.co.mathrank.domain.problem.single.read.service.SingleProblemReadModelRegisterService;
import kr.co.mathrank.domain.problem.single.read.service.SingleProblemUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SingleProblemReadMonolithEventListener {
	private final SingleProblemUpdateService singleProblemUpdateService;
	private final SingleProblemReadModelRegisterService singleProblemReadModelRegisterService;

	private String problemUpdatedEventTopic = "problem-info-updated";
	private String singleProblemSolvedEventTopic = "single-problem-solved";
	private String singlePRoblemRegisteredEventToic = "single-problem-registered";

	@Async("infoUpdatedMessageProcessingExecutor")
	@EventListener(MonolithEvent.class)
	public void listenInfoUpdatedEvent(final MonolithEvent monolithEvent) {
		if (!monolithEvent.isExpectedTopic(problemUpdatedEventTopic)) {
			return;
		}
		log.debug("[SingleProblemReadMonolithEventListener.listenInfoUpdatedEvent] Monolith event received: {}", monolithEvent);
		final Event<ProblemUpdatedEventPayload> event = Event.fromJson(monolithEvent.payload(), ProblemUpdatedEventPayload.class);
		singleProblemUpdateService.updateProblemInfo(event.getPayload().toCommand());
	}

	@Async("singleProblemSolvedMessageProcessingExecutor")
	@EventListener(MonolithEvent.class)
	public void listenSingleProblemSolvedEvent(final MonolithEvent monolithEvent) {
		if (!monolithEvent.isExpectedTopic(singleProblemSolvedEventTopic)) {
			return;
		}
		log.debug("[SingleProblemReadMonolithEventListener.listenSingleProblemSolvedEvent] Monolith event received: {}", monolithEvent);
		final Event<SingleProblemSolvedEventPayload> event = Event.fromJson(monolithEvent.payload(), SingleProblemSolvedEventPayload.class);
		singleProblemUpdateService.updateAttemptStatistics(event.getPayload().toCommand());
	}

	@Async("singleProblemRegisteredMessageProcessingExecutor")
	@EventListener(MonolithEvent.class)
	public void listenSingleProblemRegisteredEvent(final MonolithEvent monolithEvent) {
		if (!monolithEvent.isExpectedTopic(singlePRoblemRegisteredEventToic)) {
			return;
		}
		log.debug("[SingleProblemReadMonolithEventListener.listenSingleProblemRegisteredEvent] Monolith event received: {}", monolithEvent);
		final Event<SingleProblemRegisteredEventPayload> event = Event.fromJson(monolithEvent.payload(), SingleProblemRegisteredEventPayload.class);
		singleProblemReadModelRegisterService.save(event.getPayload().toCommand());
	}
}
