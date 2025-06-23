package kr.co.mathrank.domain.board.post.outbox.payload;

import kr.co.mathrank.domain.board.post.entity.Post;
import kr.co.mathrank.domain.board.post.outbox.EventType;

public interface PostEventPayloadHandler<T extends Post> {
	String createPayload(final T post, final Long eventId);
	boolean supports(EventType eventType);
}
