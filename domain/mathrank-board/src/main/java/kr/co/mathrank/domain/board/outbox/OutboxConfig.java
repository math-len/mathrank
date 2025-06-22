package kr.co.mathrank.domain.board.outbox;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@EnableAsync
class OutboxConfig {
	@Bean
	ScheduledExecutorService pendingEventExecutor() {
		return Executors.newSingleThreadScheduledExecutor();
	}

	@Bean
	ExecutorService eventAsyncPublisher() {
		return Executors.newFixedThreadPool(10);
	}
}
