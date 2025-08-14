package kr.co.mathrank.common.event.publisher.test;

import kr.co.mathrank.common.event.publisher.EventPublishException;
import kr.co.mathrank.common.event.publisher.EventPublisher;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestEventPublisher implements EventPublisher {
	@Override
	public void publish(String topic, String payload) throws EventPublishException {
		log.debug("[TestEventPublisher.publish] mock publish - topic: {}, payload: {}", topic, payload);
	}
}
