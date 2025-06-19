package kr.co.mathrank.common.outbox;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(properties = """
snowflake.node.id=1
""")
class TransactionalOutboxPublisherTest {
    @MockitoBean
    private OutboxEventConsumer outboxEventConsumer;
    @Autowired
    private TestPublisher testPublisher;

    @Test
    void 이벤트_발행시_반드시_실행된다() {
        testPublisher.publish();

        Mockito.verify(outboxEventConsumer, Mockito.atLeastOnce()).saveEvent(Mockito.any());
        Mockito.verify(outboxEventConsumer, Mockito.atLeastOnce()).publishEvent(Mockito.any());
    }
}
