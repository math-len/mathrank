package kr.co.mathrank.domain.board.post.outbox.posthandler;

import org.springframework.stereotype.Component;

import kr.co.mathrank.domain.board.post.entity.Post;
import kr.co.mathrank.domain.board.post.outbox.EventType;
import kr.co.mathrank.domain.board.post.repository.PostRepository;
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
