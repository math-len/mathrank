package kr.co.mathrank.domain.board.outbox;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import kr.co.mathrank.domain.board.entity.FreePost;
import kr.co.mathrank.domain.board.entity.Post;
import kr.co.mathrank.domain.board.repository.PostRepository;

@SpringBootTest
class OutboxEventPublisherTest {
	@Autowired
	private PostRepository postRepository;
	@Autowired
	private OutboxEventPublisher outboxEventPublisher;
	@MockitoBean
	private KafkaTemplate<String, String> kafkaTemplate;

	@AfterEach
	void clean() {
		postRepository.deleteAll();
	}

	@Test
	void 전송_성공시_아웃박스_삭제() {
		SendResult<String, String> mockSendResult = Mockito.mock(SendResult.class);
		final CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
		future.complete(mockSendResult);
		Mockito.when(kafkaTemplate.send(Mockito.anyString(), Mockito.anyString())).thenReturn(future);
		Post post = postRepository.save(
			new FreePost("title", "content", 1L, LocalDateTime.now(), Collections.emptyList(), new Outbox(
				EventType.FREE_POST_CREATED_EVENT, LocalDateTime.now())));

		outboxEventPublisher.publishMessage(post);
		try {
			Thread.sleep(500L);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		final Post renewedPost = postRepository.findById(post.getId()).get();
		Assertions.assertNull(renewedPost.getOutbox());
	}

	@Test
	void 전송_실패시_아웃박스_보존() {
		Mockito.when(kafkaTemplate.send(Mockito.anyString(), Mockito.anyString())).thenThrow(new RuntimeException());
		Post post = postRepository.save(
			new FreePost("title", "content", 1L, LocalDateTime.now(), Collections.emptyList(), new Outbox(
				EventType.FREE_POST_CREATED_EVENT, LocalDateTime.now())));

		outboxEventPublisher.publishMessage(post);
		try {
			Thread.sleep(500L);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		final Post renewedPost = postRepository.findById(post.getId()).get();
		Assertions.assertNotNull(renewedPost.getOutbox());
	}
}
