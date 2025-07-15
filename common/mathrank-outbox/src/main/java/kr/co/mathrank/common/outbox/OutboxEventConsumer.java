package kr.co.mathrank.common.outbox;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
class OutboxEventConsumer {
    private final OutboxEventRepository outboxEventRepository;
    private final KafkaEventPublisher kafkaEventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void saveEvent(final Outbox outbox) {
        outboxEventRepository.save(outbox);
        log.info("[OutboxEventConsumer.saveEvent]: event: {}", outbox);
    }

    @Async("outboxEventExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishEvent(final Outbox outbox) {
        kafkaEventPublisher.publishEvent(outbox);
        log.info("[OutboxEventConsumer.publishEvent]: event: {}", outbox);
    }
}
