package kr.co.mathrank.common.outbox;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.event.Event;
import kr.co.mathrank.common.event.EventPayload;
import kr.co.mathrank.common.snowflake.Snowflake;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * 이 클래스를 사용하여 이벤트를 발행하면 {@code Transactional Outbox Pattern}이 적용됩니다.
 */
@Component
@RequiredArgsConstructor
@Validated
public class TransactionalOutboxPublisher {
    private final ApplicationEventPublisher eventPublisher;
    private final Snowflake snowflake;

    @Transactional(propagation = Propagation.MANDATORY)
    public void publish(@NotEmpty final String topic, @NotNull final EventPayload payload) {
        final Event<EventPayload> event = Event.of(snowflake.nextId(), payload);
        final String serializedPayload = event.serialize();

        final Outbox outbox = Outbox.of(event.getEventId(), topic, serializedPayload);
        eventPublisher.publishEvent(outbox);
    }
}
