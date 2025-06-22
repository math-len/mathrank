package kr.co.mathrank.domain.board.outbox.payload;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;

import kr.co.mathrank.common.dataserializer.DataSerializer;
import kr.co.mathrank.common.event.EventPayload;
import kr.co.mathrank.domain.board.entity.ProblemQuestionPost;
import kr.co.mathrank.domain.board.outbox.EventType;

@Component
class ProblemPostEventPayloadHandler implements PostEventPayloadHandler<ProblemQuestionPost> {
	@Override
	public String createPayload(ProblemQuestionPost post, Long eventId) {
		final ProblemPostRegisteredEventPayload payload = new ProblemPostRegisteredEventPayload(eventId, post.getId(),
			post.getTitle(), post.getContent(), post.getOwnerId(), post.getCreatedAt(), post.getUpdatedAt(),
			post.getImages(), post.getQuestionId());
		return DataSerializer.serialize(payload).get();
	}

	@Override
	public boolean supports(EventType eventType) {
		return eventType.equals(EventType.PROBLEM_POST_CREATED_EVENT);
	}

	public record ProblemPostRegisteredEventPayload(Long eventId, String postId, String title, String content,
													Long ownerId, LocalDateTime createdAt, LocalDateTime updatedAt,
													List<String> images, Long problemId) implements EventPayload {
	}
}
