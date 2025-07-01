package kr.co.mathrank.domain.board.comment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.snowflake.Snowflake;
import kr.co.mathrank.domain.board.comment.dto.CommentDeleteCommand;
import kr.co.mathrank.domain.board.comment.dto.CommentRegisterCommand;
import kr.co.mathrank.domain.board.comment.dto.CommentUpdateCommand;
import kr.co.mathrank.domain.board.comment.entity.Comment;
import kr.co.mathrank.domain.board.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {
	private final CommentRepository commentRepository;
	private final Snowflake snowflake;

	public Long save(@NotNull @Valid final CommentRegisterCommand command) {
		final Long id = snowflake.nextId();
		final Comment comment = Comment.of(id, command.postId(), command.content(), command.userId(), command.images());
		commentRepository.save(comment);
		return comment.getId();
	}

	@Transactional
	public void update(@NotNull @Valid final CommentUpdateCommand command) {
		final Comment comment = commentRepository.findWithImages(command.commentId())
			.orElseThrow(() -> new IllegalArgumentException("Comment not found with id: " + command.commentId()));
		isOwner(comment, command.userId());

		comment.setContent(command.content());
		comment.updateImages(command.images());
	}

	@Transactional
	public void delete(@NotNull @Valid final CommentDeleteCommand command) {
		final Comment comment = commentRepository.findWithImages(command.commentId())
			.orElseThrow(() -> new IllegalArgumentException("Comment not found with id: " + command.commentId()));
		isOwner(comment, command.requestMemberId());

		commentRepository.delete(comment);
	}

	private void isOwner(final Comment comment, final Long userId) {
		if (!comment.getUserId().equals(userId)) {
			throw new IllegalArgumentException("You do not have permission to perform this action.");
		}
	}
}
