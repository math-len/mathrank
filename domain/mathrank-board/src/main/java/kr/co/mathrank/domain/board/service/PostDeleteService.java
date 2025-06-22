package kr.co.mathrank.domain.board.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.board.dto.PostDeleteCommand;
import kr.co.mathrank.domain.board.entity.Post;
import kr.co.mathrank.domain.board.outbox.EventType;
import kr.co.mathrank.domain.board.outbox.Outbox;
import kr.co.mathrank.domain.board.repository.PostRepository;
import lombok.RequiredArgsConstructor;

@Service
@Validated
@RequiredArgsConstructor
public class PostDeleteService {
	private final PostRepository postRepository;

	public void delete(@NotNull @Valid final PostDeleteCommand command) {
		postRepository.findByIdAndDeletedIsFalse(command.postId())
			.ifPresentOrElse(post -> {
				validateOwner(command.requestMemberId(), post);
				logicalDelete(post);
			}, () -> {throw new IllegalArgumentException();});
	}

	private void logicalDelete(final Post post) {
		post.setDeleted(true);
		post.setOutbox(new Outbox(EventType.POST_DELETED_EVENT, LocalDateTime.now()));
		postRepository.save(post);
	}

	private void validateOwner(final Long requestMemberId, final Post post) {
		if (requestMemberId.equals(post.getOwnerId())) {
			return;
		}
		throw new IllegalArgumentException();
	}
}
