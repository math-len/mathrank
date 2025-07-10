package kr.co.mathrank.common.outbox;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class PendingEventWorker {
    private final OutboxEventRepository eventOutboxRepository;
    private final KafkaEventPublisher kafkaEventPublisher;

    @Scheduled(initialDelayString = "${event.pending.initial:30}", fixedDelayString = "${event.pending.interval:30}", timeUnit = TimeUnit.SECONDS, scheduler = "pendingMessageWorker")
    public void publishPendingEvents() {
        eventOutboxRepository.findAll()
                .forEach(kafkaEventPublisher::publishEvent);
        log.info("Publishing pending events completed");
    }
}
