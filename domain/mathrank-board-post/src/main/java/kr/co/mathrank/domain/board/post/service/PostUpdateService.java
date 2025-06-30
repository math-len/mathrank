package kr.co.mathrank.domain.board.post.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.board.post.dto.PostUpdateCommand;
import kr.co.mathrank.domain.board.post.entity.Post;
import kr.co.mathrank.domain.board.post.outbox.EventType;
import kr.co.mathrank.domain.board.post.outbox.Outbox;
import kr.co.mathrank.domain.board.post.outbox.OutboxEventPublisher;
import kr.co.mathrank.domain.board.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;

@Service
@Validated
@RequiredArgsConstructor
public class PostUpdateService {
	private final PostRepository postRepository;
	private final OutboxEventPublisher outboxEventPublisher;

	public void update(@NotNull @Valid final PostUpdateCommand command) {
		postRepository.findByIdAndDeletedIsFalse(command.postId())
			.ifPresentOrElse(post -> {
				validate(command.memberId(), post);

				post.setUpdatedAt(LocalDateTime.now());
				post.resetImages(command.images());
				post.setTitle(command.title());
				post.setContent(command.content());

				post.setOutbox(new Outbox(EventType.POST_UPDATED_EVENT, LocalDateTime.now()));

				postRepository.save(post);
				outboxEventPublisher.publishMessage(post);
			}, () -> {throw new IllegalArgumentException("Post not found");});
	}

	private void validate(final Long requestMemberId, final Post post) {
		if (requestMemberId.equals(post.getOwnerId())) {
			return;
		}
		throw new IllegalArgumentException("Post owner is not the owner of this post");
	}
}
