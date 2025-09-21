package kr.co.mathrank.domain.board.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.role.Role;
import kr.co.mathrank.domain.board.dto.PostDeleteCommand;
import kr.co.mathrank.domain.board.entity.Post;
import kr.co.mathrank.domain.board.exception.CannotFoundPostException;
import kr.co.mathrank.domain.board.exception.CannotDeletePostException;
import kr.co.mathrank.domain.board.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class PostDeleteService {
	private final PostRepository postRepository;

	@Transactional
	public void delete(@NotNull @Valid final PostDeleteCommand command) {
		final Post post = getPost(command.postId());

		if (isOwner(command.role(), command.memberId(), post)) {
			postRepository.delete(post);
			log.info("[PostDeleteService.delete] post deleted - postId: {}, requestMemberId: {}, requestMemberRole: {}", post.getId(), command.memberId(), command.role());
			return;
		}
		log.info("[PostDeleteService.delete] post not deleted - postId: {}, requestMemberId: {}, requestMemberRole: {}", post.getId(), command.memberId(), command.role());
		throw new CannotDeletePostException();
	}

	private Post getPost(final Long postId) {
		return postRepository.findById(postId)
			.orElseThrow(() -> {
				log.info("[PostDeleteService.delete] cannot found post - postId: {}", postId);
				return new CannotFoundPostException();
			});
	}

	private boolean isOwner(final Role role, final Long requestMemberId, final Post post) {
		// 주인장이면 통과!
		if (role == Role.ADMIN) {
			return true;
		}

		// 일반 사용자일 경우 본인 글만 지울 수 있음
		return requestMemberId.equals(post.getUserId());
	}
}
