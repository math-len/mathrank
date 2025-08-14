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

/**
 * 모놀리식 환경에서 발생하는 단일 문제(Single Problem) 관련 이벤트를 구독하여
 * Read Model을 갱신하거나 신규 등록하는 리스너.
 *
 * - problem-info-updated: 문제 정보 변경
 * - single-problem-solved: 문제 풀이 통계 업데이트
 * - single-problem-registered: 단일 문제 신규 등록
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SingleProblemReadMonolithEventListener {
	private final SingleProblemUpdateService singleProblemUpdateService;
	private final SingleProblemReadModelRegisterService singleProblemReadModelRegisterService;

	private String problemUpdatedEventTopic = "problem-info-updated";
	private String singleProblemSolvedEventTopic = "single-problem-solved";
	private String singlePRoblemRegisteredEventToic = "single-problem-registered";

	/**
	 * 문제 정보가 업데이트되는 이벤트를 처리
	 * - Topic: problem-info-updated
	 * - 처리 로직: 문제 기본 정보(제목, 이미지, 난이도 등)를 업데이트
	 */
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

	/**
	 * 문제 풀이 통계(시도 횟수, 정답 횟수 등)를 업데이트하는 이벤트 처리
	 * - Topic: single-problem-solved
	 * - 처리 로직: 해당 문제의 시도/정답 통계 반영
	 */
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

	/**
	 * 신규 단일 문제 등록 이벤트 처리
	 * - Topic: single-problem-registered
	 * - 처리 로직: Read Model에 신규 문제 데이터 저장
	 */
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
