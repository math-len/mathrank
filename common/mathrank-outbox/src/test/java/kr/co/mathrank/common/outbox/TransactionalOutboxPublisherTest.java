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
	spring.jpa.show-sql=true
	""")
public class TransactionalOutboxPublisherTest {
	@MockitoBean
	private KafkaTemplate<String, String> kafkaTemplate;

	@Autowired
	private TestPublisher testPublisher;
	@Autowired
	private OutboxEventRepository outboxEventRepository;

	@AfterEach
	public void clearDatabase() {
		outboxEventRepository.deleteAll();
	}

	@Test
	void 이벤트_발행_성공_시_아웃박스_삭제() {
		Mockito.doReturn(CompletableFuture.completedFuture(null))
			.when(kafkaTemplate)
			.send(Mockito.anyString(), Mockito.anyString());
		testPublisher.publish();

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		Assertions.assertThat(outboxEventRepository.findAll().size()).isEqualTo(0);
	}

	@Test
	void 이벤트_발행_실패시_아웃박스가_저장() {
		Mockito.doThrow(new RuntimeException()).when(kafkaTemplate).send(Mockito.anyString(), Mockito.anyString());
		testPublisher.publish();

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		Assertions.assertThat(outboxEventRepository.findAll().size()).isEqualTo(1);
	}
}
