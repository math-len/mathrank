package kr.co.mathrank.domain.board.post.outbox.posthandler;

import kr.co.mathrank.domain.board.post.entity.Post;
import kr.co.mathrank.domain.board.post.outbox.EventType;

interface PublishedEventPostHandler {
	void handle(Post post);

	boolean supports(EventType eventType);
}
