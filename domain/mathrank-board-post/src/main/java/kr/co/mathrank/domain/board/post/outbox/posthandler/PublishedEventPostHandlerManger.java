package kr.co.mathrank.domain.board.post.outbox.posthandler;

import java.util.List;

import org.springframework.stereotype.Component;

import kr.co.mathrank.domain.board.post.entity.Post;
import kr.co.mathrank.domain.board.post.outbox.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PublishedEventPostHandlerManger {
	private final List<PublishedEventPostHandler> handlers;

	public void handle(final Post post, final EventType eventType) {
		handlers.stream()
			.filter(publishedEventPostHandler -> publishedEventPostHandler.supports(eventType))
			.findFirst()
			.ifPresentOrElse(publishedEventPostHandler -> publishedEventPostHandler.handle(post),
				() -> log.debug("[PublishedEventPostHandleManager.handle] No published event post handler found for: {}, {}", post, eventType));
	}
}
