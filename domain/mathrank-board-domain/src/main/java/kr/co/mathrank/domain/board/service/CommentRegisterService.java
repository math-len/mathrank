package kr.co.mathrank.domain.board.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.board.dto.CommentRegisterCommand;
import kr.co.mathrank.domain.board.entity.Comment;
import kr.co.mathrank.domain.board.entity.Post;
import kr.co.mathrank.domain.board.exception.CannotFoundPostException;
import kr.co.mathrank.domain.board.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class CommentRegisterService {
	private final PostRepository postRepository;

	@Transactional
	public Long register(@NotNull @Valid final CommentRegisterCommand command) {
		final Post post = getPost(command.postId());
		final Comment comment = post.addComment(command.content(), command.memberId());
		postRepository.flush();
		log.info("[CommentRegisterService.register] comment registered - postId: {}, commentId: {}", post.getId(), comment.getId());
		return comment.getId();
	}

	private Post getPost(final Long postId) {
		return postRepository.findById(postId)
			.orElseThrow(() -> {
				log.info("[CommentRegisterService.getPost] cannot found post - postId: {}", postId);
				return new CannotFoundPostException();
			});
	}
}
