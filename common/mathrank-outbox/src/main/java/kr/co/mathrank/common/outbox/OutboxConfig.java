package kr.co.mathrank.common.outbox;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableAsync
@EnableScheduling
@Slf4j
class OutboxConfig {
    @Bean
    public Executor outboxEventExecutor(final OutboxEventExecutorProperties properties) {
        log.info("initialize outbox event executor: {}", properties);
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(properties.getCorePoolSize());
        executor.setMaxPoolSize(properties.getMaxPoolSize());
        executor.setQueueCapacity(properties.getQueueCapacity());
        executor.setThreadNamePrefix(properties.getNamePrefix());
        executor.initialize();

        return executor;
    }

    @Bean
    ScheduledExecutorService pendingMessageWorker() {
        return Executors.newSingleThreadScheduledExecutor();
    }

    @Configuration
    @ConfigurationProperties("outbox.worker")
    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    private static class OutboxEventExecutorProperties {
        private Integer corePoolSize = 20;
        private Integer maxPoolSize = 30;
        private Integer queueCapacity = 100;
        private String namePrefix = "outbox-pub-";
    }
}
