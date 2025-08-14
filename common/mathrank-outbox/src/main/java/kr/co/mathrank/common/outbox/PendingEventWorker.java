package kr.co.mathrank.common.outbox;

import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
class PendingEventWorker {
    private final OutboxEventRepository eventOutboxRepository;
    private final OutboxEventPublisher outboxEventPublisher;

    @Scheduled(initialDelayString = "${event.pending.initial:30}", fixedDelayString = "${event.pending.interval:30}", timeUnit = TimeUnit.SECONDS, scheduler = "pendingMessageWorker")
    public void publishPendingEvents() {
        eventOutboxRepository.findAll()
                .forEach(outboxEventPublisher::publishEvent);
        log.info("Publishing pending events completed");
    }
}
