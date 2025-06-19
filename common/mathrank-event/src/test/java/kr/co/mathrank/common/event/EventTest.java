package kr.co.mathrank.common.event;

import java.time.LocalDateTime;
import kr.co.mathrank.common.dataserializer.DataSerializer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class EventTest {
    private static final LocalDateTime NOW = LocalDateTime.of(1999, 11, 21, 10, 40);

    @Test
    void 직렬화_테스트() {
        final Event event = Event.of(1L, new ProducerAccountRegisteredEventPayload(1L, true, 10, NOW));
        Assertions.assertThat(event).isNotNull();
    }

    @Test
    void 다른_타입으로_역직렬화_된다() {
        final Event event = Event.of(1L, new ProducerAccountRegisteredEventPayload(1L, true, 10, NOW));
        final String json = event.serialize();

        final Event<ConsumerAccountRegisteredEventPayload> eventPayloadEvent = Event.fromJson(json, ConsumerAccountRegisteredEventPayload.class);
        Assertions.assertThat(eventPayloadEvent).isNotNull();

        Assertions.assertThat(eventPayloadEvent.getPayload().accountId).isEqualTo(1L);
        Assertions.assertThat(eventPayloadEvent.getPayload().male).isEqualTo(true);
        Assertions.assertThat(eventPayloadEvent.getPayload().age).isEqualTo(10);
        Assertions.assertThat(eventPayloadEvent.getPayload().registeredTime).isEqualTo(NOW);
    }

    @Test
    void 잘못된_역직렬화시_예외처리() {
        try (MockedStatic<DataSerializer> dataSerializerMockedStatic = Mockito.mockStatic(DataSerializer.class)){
            dataSerializerMockedStatic.when(() -> DataSerializer.deserialize(Mockito.any(), Mockito.any())).thenThrow(IllegalArgumentException.class);

            Assertions.assertThatThrownBy(() -> Event.fromJson("test", ConsumerAccountRegisteredEventPayload.class)).isInstanceOf(EventException.class);
        }
    }

    private record ProducerAccountRegisteredEventPayload(
            Long accountId,
            Boolean male,
            Integer age,
            LocalDateTime registeredTime
    ) implements EventPayload{
    }

    private record ConsumerAccountRegisteredEventPayload(
            Long accountId,
            Boolean male,
            Integer age,
            LocalDateTime registeredTime
    ) implements EventPayload{
    }
}
