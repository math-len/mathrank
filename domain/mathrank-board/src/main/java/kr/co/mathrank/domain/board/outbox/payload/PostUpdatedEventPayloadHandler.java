package kr.co.mathrank.domain.board.outbox.payload;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;

import kr.co.mathrank.common.dataserializer.DataSerializer;
import kr.co.mathrank.common.event.EventPayload;
import kr.co.mathrank.domain.board.entity.Post;
import kr.co.mathrank.domain.board.outbox.EventType;

@Component
class PostUpdatedEventPayloadHandler implements PostEventPayloadHandler<Post> {
	@Override
	public String createPayload(Post post, Long eventId) {
		final PostUpdatedEventPayload payload = new PostUpdatedEventPayload(eventId, post.getId(), post.getTitle(),
			post.getContent(), post.getOwnerId(), post.getCreatedAt(), post.getUpdatedAt(), post.getImages());
		return DataSerializer.serialize(payload).get();
	}

	@Override
	public boolean supports(EventType eventType) {
		return eventType.equals(EventType.POST_UPDATED_EVENT);
	}

	public record PostUpdatedEventPayload(Long eventId, String postId, String title, String content, Long ownerId,
										  LocalDateTime createdAt, LocalDateTime updatedAt, List<String> images)
		implements EventPayload {
	}
}
