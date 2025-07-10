package kr.co.mathrank.common.outbox;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class KafkaEventPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final OutboxEventRepository eventOutboxRepository;

    public void publishEvent(final Outbox outbox) {
        try {
            kafkaTemplate.send(
                    outbox.getTopic(),
                    outbox.getPayload()
            ).get(1L, TimeUnit.SECONDS);
            eventOutboxRepository.delete(outbox);
            log.info("Published outbox event {}", outbox);
        } catch (Exception e) {
            log.error("Error publishing outbox {}", outbox, e);
            throw new RuntimeException(e);
        }
    }
}
