package kr.co.mathrank.domain.board.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.board.dto.PostDeleteCommand;
import kr.co.mathrank.domain.board.entity.Post;
import kr.co.mathrank.domain.board.repository.PostRepository;
import lombok.RequiredArgsConstructor;

@Service
@Validated
@RequiredArgsConstructor
public class PostDeleteService {
	private final PostRepository postRepository;

	public void delete(@NotNull @Valid final PostDeleteCommand command) {
		postRepository.findById(command.postId())
			.ifPresentOrElse(post -> {
				validateOwner(command.requestMemberId(), post);
				postRepository.delete(post);
			}, () -> {throw new IllegalArgumentException();});
	}

	private void validateOwner(final Long requestMemberId, final Post post) {
		if (requestMemberId.equals(post.getOwnerId())) {
			return;
		}
		throw new IllegalArgumentException();
	}
}
