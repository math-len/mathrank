package kr.co.mathrank.domain.board.post.outbox;

import java.util.concurrent.TimeUnit;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import kr.co.mathrank.domain.board.post.entity.Post;
import kr.co.mathrank.domain.board.post.outbox.payload.PostEventPayloadManager;
import kr.co.mathrank.domain.board.post.outbox.posthandler.PublishedEventPostHandlerManger;
import kr.co.mathrank.domain.board.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEventPublisher {
	private final PostRepository postRepository;
	private final PostEventPayloadManager messagePayloadManager;
	private final PublishedEventPostHandlerManger publishedEventPostHandlerManger;

	private final KafkaTemplate<String, String> kafkaTemplate;

	@Async("eventAsyncPublisher")
	public void publishMessage(final Post post) {
		final EventType eventType = post.getOutbox().getEventType();
		final String message = messagePayloadManager.extractPayload(post);
		log.debug("[OutboxEventPublisher.publishMessage]: {}", message);

		try {
			kafkaTemplate.send(eventType.getTopic(), message)
				.get(1L, TimeUnit.SECONDS);
			clearOutbox(post);
			publishedEventPostHandlerManger.handle(post, eventType);
		} catch (Exception e) {
			log.error("[OutboxEventExecutor.publishMessage]: error occurred {}", post, e);
			throw new RuntimeException(e);
		}
	}

	private void clearOutbox(final Post post) {
		post.clearOutbox();
		postRepository.save(post);
	}
}
