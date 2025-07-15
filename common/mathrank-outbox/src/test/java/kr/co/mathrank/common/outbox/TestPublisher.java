package kr.co.mathrank.common.outbox;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.co.mathrank.common.event.EventPayload;

@Component
public class TestPublisher {
    @Autowired
    private TransactionalOutboxPublisher outboxPublisher;

    @Transactional
    public void publish() {
        outboxPublisher.publish("test", new TestPayload("testName"));
    }

    private record TestPayload(
            String name
    ) implements EventPayload {
    }
}
