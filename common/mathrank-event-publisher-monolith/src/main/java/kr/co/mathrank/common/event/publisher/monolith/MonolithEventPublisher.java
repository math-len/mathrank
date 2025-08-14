package kr.co.mathrank.common.event.publisher.monolith;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import kr.co.mathrank.common.event.publisher.EventPublishException;
import kr.co.mathrank.common.event.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Validated
@RequiredArgsConstructor
public class MonolithEventPublisher implements EventPublisher {
	private final ApplicationEventPublisher eventPublisher;

	@Override
	public void publish(String topic, String payload) throws EventPublishException {
		log.debug("[MonolithEventPublisher.publish] publishing event to monolith topic: {} payload: {}", topic, payload);
		eventPublisher.publishEvent(new MonolithEvent(topic, payload));
	}
}
