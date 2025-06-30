package kr.co.mathrank.domain.board.post.outbox.payload;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;

import kr.co.mathrank.common.dataserializer.DataSerializer;
import kr.co.mathrank.common.event.EventPayload;
import kr.co.mathrank.domain.board.post.entity.FreePost;
import kr.co.mathrank.domain.board.post.outbox.EventType;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class FreePostEventPayloadHandler implements PostEventPayloadHandler<FreePost> {
	@Override
	public String createPayload(FreePost post, Long eventId) {
		final FreePostRegisteredEventPayload payload = new FreePostRegisteredEventPayload(eventId, post.getId(),
			post.getTitle(), post.getContent(), post.getOwnerId(), post.getCreatedAt(), post.getUpdatedAt(),
			post.getImages());
		return DataSerializer.serialize(payload).get();
	}

	@Override
	public boolean supports(EventType eventType) {
		return eventType.equals(EventType.FREE_POST_CREATED_EVENT);
	}

	public record FreePostRegisteredEventPayload(Long eventId, String postId, String title, String content,
												 Long ownerId, LocalDateTime createdAt, LocalDateTime updatedAt,
												 List<String> images) implements EventPayload {
	}
}
