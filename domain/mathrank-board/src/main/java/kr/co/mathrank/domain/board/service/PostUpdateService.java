package kr.co.mathrank.domain.board.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.board.dto.PostUpdateCommand;
import kr.co.mathrank.domain.board.entity.Post;
import kr.co.mathrank.domain.board.repository.PostRepository;
import lombok.RequiredArgsConstructor;

@Service
@Validated
@RequiredArgsConstructor
public class PostUpdateService {
	private final PostRepository postRepository;

	public void update(@NotNull @Valid final PostUpdateCommand command) {
		postRepository.findById(command.postId())
			.ifPresentOrElse(post -> {
				validate(command.memberId(), post);

				post.setUpdatedAt(LocalDateTime.now());
				post.resetImages(command.images());
				post.setTitle(command.title());
				post.setContent(command.content());

				postRepository.save(post);
			}, () -> {throw new IllegalArgumentException("Post not found");});
	}

	private void validate(final Long requestMemberId, final Post post) {
		if (requestMemberId.equals(post.getOwnerId())) {
			return;
		}
		throw new IllegalArgumentException("Post owner is not the owner of this post");
	}
}
