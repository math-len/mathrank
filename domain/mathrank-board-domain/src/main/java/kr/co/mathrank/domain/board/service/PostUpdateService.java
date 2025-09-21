package kr.co.mathrank.domain.board.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.board.dto.PostUpdateCommand;
import kr.co.mathrank.domain.board.entity.Post;
import kr.co.mathrank.domain.board.exception.CannotFoundPostException;
import kr.co.mathrank.domain.board.exception.CannotUpdatePostException;
import kr.co.mathrank.domain.board.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class PostUpdateService {
	private final PostRepository postRepository;

	@Transactional
	public void update(@NotNull @Valid final PostUpdateCommand command) {
		final Post post = getPost(command.postId());
		if (!isOwner(command.memberId(), post)) {
			log.info("[PostUpdateService.update] cannot update post - postId: {}, postOwnerId: {}, requestMemberId: {}", post.getId(), post.getMemberId(), command.memberId());
			throw new CannotUpdatePostException();
		}
		post.setTitle(command.title());
		post.setContent(command.content());
		log.info("[PostUpdateService.update] updated post - postId: {}, postOwnerId: {}, requestMemberId: {}", post.getId(), post.getMemberId(), command.memberId());
	}

	private Post getPost(final Long postId) {
		return postRepository.findById(postId)
			.orElseThrow(() -> {
				log.info("[PostUpdateService.update] cannot found post - postId: {}", postId);
				return new CannotFoundPostException();
			});
	}

	private boolean isOwner(final Long requestMemberId, final Post post) {
		// 일반 사용자일 경우 본인 글만 지울 수 있음
		// 주인장도 수정은 불가!
		return requestMemberId.equals(post.getMemberId());
	}
}
