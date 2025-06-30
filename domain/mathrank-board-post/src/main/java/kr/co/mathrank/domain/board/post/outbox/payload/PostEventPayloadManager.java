package kr.co.mathrank.domain.board.post.outbox.payload;

import java.util.List;

import org.springframework.stereotype.Component;

import kr.co.mathrank.common.snowflake.Snowflake;
import kr.co.mathrank.domain.board.post.entity.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings({"unchecked", "rawtypes"})
@Slf4j
@Component
@RequiredArgsConstructor
public class PostEventPayloadManager {
	private final List<PostEventPayloadHandler> messagePayloadHandlers;
	private final Snowflake snowflake;

	public String extractPayload(Post post) {
		try {
			final Long eventId = snowflake.nextId();
			return messagePayloadHandlers.stream()
				.filter(postMessagePayloadHandler -> postMessagePayloadHandler.supports(post.getOutbox().getEventType()))
				.findFirst()
				.map(postMessagePayloadHandler -> postMessagePayloadHandler.createPayload(post, eventId))
				.orElseThrow(IllegalArgumentException::new);
		} catch (Exception e) {
			log.error("[MessagePayloadManager.extractPayload] post: {}", post, e);
			throw e;
		}
	}
}
