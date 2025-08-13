package kr.co.mathrank.common.event.publisher.monolith;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public record MonolithEvent(
	String topic,
	String payload
) {
	public boolean isExpectedTopic(String topic) {
		log.debug("[MonolithEvent.isExpectedEvent] topic status - expected: {}, real: {}", topic, this.topic);
		return topic.equals(this.topic);
	}
}
