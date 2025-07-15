package kr.co.mathrank.common.event;

import java.time.LocalDateTime;

import kr.co.mathrank.common.dataserializer.DataSerializer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
public class Event<T extends EventPayload> {
    private Long eventId;
    private LocalDateTime createdAt;
    private T payload;

    public static <T extends EventPayload> Event<T> of(final Long eventId, final EventPayload payload) {
        final Event event = new Event();
        event.eventId = eventId;
        event.createdAt = LocalDateTime.now();
        event.payload = payload;

        return event;
    }

    public static <T extends EventPayload> Event<T> fromJson(final String json, final Class<T> payloadClazz) {
        log.debug("fromJson: {}, clazz: {}", json, payloadClazz);
        final Event<T> event = new Event();
        try {
            final RawEvent raw = DataSerializer.deserialize(json, RawEvent.class).orElseThrow();
            final T payload = DataSerializer.deserialize(raw.payload, payloadClazz).orElseThrow();

            event.eventId = raw.eventId;
            event.createdAt = raw.createdAt;
            event.payload = payload;
        } catch (IllegalArgumentException e) {
            log.error("[Event.fromJson] cannot deserialize event: {}", json, e);
            throw new EventException(e);
        }

        return event;
    }

    public String serialize() {
        return DataSerializer.serialize(this).orElseThrow();
    }

    @Getter
    private static class RawEvent {
        private Long eventId;
        private LocalDateTime createdAt;
        private Object payload;
    }
}
