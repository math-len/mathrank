package kr.co.mathrank.common.event.publisher.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import kr.co.mathrank.common.event.publisher.EventPublishException;
import kr.co.mathrank.common.event.publisher.EventPublisher;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class EventPublisherTestConfiguration {
	@Bean
	@Primary
	public EventPublisher testEventPublisher() {
		return new TestEventPublisher();
	}
}
