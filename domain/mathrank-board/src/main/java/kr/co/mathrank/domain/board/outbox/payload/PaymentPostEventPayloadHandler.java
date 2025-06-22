package kr.co.mathrank.domain.board.outbox.payload;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;

import kr.co.mathrank.common.dataserializer.DataSerializer;
import kr.co.mathrank.common.event.EventPayload;
import kr.co.mathrank.domain.board.entity.PurchasePost;
import kr.co.mathrank.domain.board.outbox.EventType;

@Component
class PaymentPostEventPayloadHandler implements PostEventPayloadHandler<PurchasePost> {
	@Override
	public String createPayload(PurchasePost post, Long eventId) {
		final PaymentPostRegisteredEventPayload payload = new PaymentPostRegisteredEventPayload(eventId, post.getId(),
			post.getTitle(), post.getContent(), post.getOwnerId(), post.getCreatedAt(), post.getUpdatedAt(),
			post.getImages(), post.getPurchaseId());
		return DataSerializer.serialize(payload).get();
	}

	@Override
	public boolean supports(EventType eventType) {
		return eventType.equals(EventType.PURCHASE_POST_CREATED_EVENT);
	}

	public record PaymentPostRegisteredEventPayload(Long eventId, String postId, String title, String content,
													Long ownerId, LocalDateTime createdAt, LocalDateTime updatedAt,
													List<String> images, Long paymentId) implements EventPayload {
	}
}
