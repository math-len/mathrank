package kr.co.mathrank.app.consumer.problem.single.read.consumer.monolith;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@Configuration
public class SingleProblemReadModelMonolithEventListenerConfig {
	@Bean
	ExecutorService infoUpdatedMessageProcessingExecutor() {
		return Executors.newFixedThreadPool(3);
	}

	@Bean
	ExecutorService singleProblemSolvedMessageProcessingExecutor() {
		return Executors.newFixedThreadPool(20);
	}

	@Bean
	ExecutorService singleProblemRegisteredMessageProcessingExecutor() {
		return Executors.newFixedThreadPool(3);
	}
}
