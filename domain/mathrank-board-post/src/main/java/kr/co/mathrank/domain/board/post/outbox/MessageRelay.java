package kr.co.mathrank.domain.board.post.outbox;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import kr.co.mathrank.domain.board.post.entity.Post;
import kr.co.mathrank.domain.board.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class MessageRelay {
	private final PostRepository postRepository;
	private final OutboxEventPublisher outboxEventExecutor;

	@Scheduled(initialDelay = 10, fixedDelay = 10, timeUnit = TimeUnit.SECONDS, scheduler = "pendingEventExecutor")
	public void sendPendingEvents() {
		final List<Post> posts = postRepository.findAllContainsOutbox();
		posts.forEach(outboxEventExecutor::publishMessage);
	}
}
