package kr.co.mathrank.app.consumer.problem.single;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@EnableKafka
public class SingleProblemReadModelConsumerConfiguration {
	@Bean
	public DefaultErrorHandler errorHandler(KafkaTemplate<String, String> template) {
		// 일시적인 오류에 대비한 재시도 정책을 설정합니다. (예: 1초 간격, 최대 3번)
		final FixedBackOff backOff = new FixedBackOff(1000L, 3L);

		final DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(template); // {topic}.DLT

		return new DefaultErrorHandler(recoverer, backOff);
	}
}
