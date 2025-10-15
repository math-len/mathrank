package kr.co.mathrank.app.consumer.point;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import kr.co.mathrank.common.event.Event;
import kr.co.mathrank.common.event.publisher.monolith.MonolithEvent;
import kr.co.mathrank.domain.point.dto.PointConsumeCommand;
import kr.co.mathrank.domain.point.service.PointConsumeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
class PointMonolithEventConsumer {
	private static final String CONTENT_ORDER_REGISTERED_EVENT = "mathrank-content-order-registered";
	private final PointConsumeService pointConsumeService;

	@EventListener
	@Async
	public void consumeContentOrderRegisteredEvent(final MonolithEvent monolithEvent) {
		// 콘텐츠 주문 등록 이벤트 수신
		if (monolithEvent.isExpectedTopic(CONTENT_ORDER_REGISTERED_EVENT)) {
			log.info("[PointMonolithEventConsumer.consumeContentOrderRegisteredEvent] consume point consume event - event: {}", monolithEvent);
			final Event<Events.ContentOrderRegisteredEvent> eventEvent = Event.fromJson(monolithEvent.payload(), Events.ContentOrderRegisteredEvent.class);
			pointConsumeService.consume(new PointConsumeCommand(
				eventEvent.getPayload().orderId(),
				eventEvent.getPayload().memberId(),
				eventEvent.getPayload().contentPointCost())
			);
		}
	}
}
