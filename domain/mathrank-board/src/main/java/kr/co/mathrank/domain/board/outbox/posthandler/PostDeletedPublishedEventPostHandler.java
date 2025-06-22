package kr.co.mathrank.domain.board.outbox.posthandler;

import org.springframework.stereotype.Component;

import kr.co.mathrank.domain.board.entity.Post;
import kr.co.mathrank.domain.board.outbox.EventType;
import kr.co.mathrank.domain.board.repository.PostRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class PostDeletedPublishedEventPostHandler implements PublishedEventPostHandler {
	private final PostRepository postRepository;

	@Override
	public void handle(Post post) {
		postRepository.delete(post);
	}

	@Override
	public boolean supports(EventType eventType) {
		return eventType.equals(EventType.POST_DELETED_EVENT);
	}
}
