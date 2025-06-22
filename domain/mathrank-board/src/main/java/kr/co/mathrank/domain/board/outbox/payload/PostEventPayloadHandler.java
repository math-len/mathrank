package kr.co.mathrank.domain.board.outbox.payload;

import kr.co.mathrank.domain.board.entity.Post;
import kr.co.mathrank.domain.board.outbox.EventType;

public interface PostEventPayloadHandler<T extends Post> {
	String createPayload(final T post, final Long eventId);
	boolean supports(EventType eventType);
}
