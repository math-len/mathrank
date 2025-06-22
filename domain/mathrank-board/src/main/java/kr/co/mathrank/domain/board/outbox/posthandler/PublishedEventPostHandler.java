package kr.co.mathrank.domain.board.outbox.posthandler;

import kr.co.mathrank.domain.board.entity.Post;
import kr.co.mathrank.domain.board.outbox.EventType;

interface PublishedEventPostHandler {
	void handle(Post post);

	boolean supports(EventType eventType);
}
