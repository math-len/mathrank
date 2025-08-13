package kr.co.mathrank.common.outbox;

import org.springframework.stereotype.Component;

import kr.co.mathrank.common.event.publisher.EventPublishException;
import kr.co.mathrank.common.event.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
class OutboxEventPublisher {
    private final OutboxEventRepository eventOutboxRepository;
    private final EventPublisher eventPublisher;

    public void publishEvent(final Outbox outbox) {
        try {
            eventPublisher.publish(outbox.getTopic(), outbox.getPayload());
            eventOutboxRepository.delete(outbox);
            log.info("Published outbox event {}", outbox);
        } catch (EventPublishException e) {
            log.error("Error publishing outbox {}", outbox, e);
            throw new RuntimeException(e);
        }
    }
}
