package kr.co.mathrank.app.consumer.content;

import java.math.BigDecimal;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import kr.co.mathrank.common.event.Event;
import kr.co.mathrank.common.event.publisher.monolith.MonolithEvent;
import kr.co.mathrank.domain.contents.service.ContentOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContentMonolithConsumer {
	private final ContentOrderService contentOrderService;

	@EventListener
	@Async
	public void consumePointConsumeSucceededEvent(final MonolithEvent monolithEvent) {
		if (monolithEvent.isExpectedTopic("mathrank-point-consume-succeeded")) {
			log.info("[ContentMonolithConsumer.consumePointConsumeSucceededEvent] received event: {}", monolithEvent);
			final Event<Events.PointConsumeSucceededEvent> event = Event.fromJson(monolithEvent.payload(), Events.PointConsumeSucceededEvent.class);

			contentOrderService.completeOrderToSucceed(
				event.getPayload().orderId(),
				BigDecimal.valueOf(event.getPayload().consumedPointAmount())
			);
		}
	}

	@EventListener
	@Async
	public void consumePointConsumeFailedEvent(final MonolithEvent monolithEvent) {
		if (monolithEvent.isExpectedTopic("mathrank-point-consume-failed")) {
			log.info("[ContentMonolithConsumer.consumePointConsumeFailedEvent] received event: {}", monolithEvent);
			final Event<Events.PointConsumeFailedEvent> event = Event.fromJson(monolithEvent.payload(), Events.PointConsumeFailedEvent.class);

			contentOrderService.completeOrderToFailed(event.getPayload().orderId());
		}
	}
}
