package kr.co.mathrank.common.outbox;

import java.util.concurrent.CompletableFuture;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(properties = """
	snowflake.node.id=1
	event.pending.initial=1
	spring.jpa.show-sql=true
	""")
class PendingEventWorkerTest {
	@MockitoBean
	private KafkaTemplate<String, String> kafkaTemplate;

	@Autowired
	private OutboxEventRepository outboxEventRepository;

	@AfterEach
	public void clearDatabase() {
		outboxEventRepository.deleteAll();
	}

	@Test
	void 이벤트_발행_실패시_주기적으로_처리된다() {
		Mockito.doReturn(CompletableFuture.completedFuture(null))
			.when(kafkaTemplate)
			.send(Mockito.anyString(), Mockito.anyString());
		outboxEventRepository.save(Outbox.of(1L, "testTopic", "testPayload"));

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		Assertions.assertThat(outboxEventRepository.findAll().size()).isEqualTo(0);
	}
}
