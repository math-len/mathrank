package kr.co.mathrank.domain.board.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.board.dto.CommentUpdateCommand;
import kr.co.mathrank.domain.board.entity.Comment;
import kr.co.mathrank.domain.board.exception.CannotFoundCommentException;
import kr.co.mathrank.domain.board.exception.CannotUpdateCommentException;
import kr.co.mathrank.domain.board.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class CommentUpdateService {
	private final CommentRepository commentRepository;

	/**
	 * 본인의 댓글만 수정 가능
	 * @param command
	 */
	@Transactional
	public void update(@NotNull @Valid final CommentUpdateCommand command) {
		final Comment comment = getComment(command.commentId());
		if (!isOwner(comment, command.memberId())) {
			log.info("[CommentUpdateService.update] cannot update comment - commentId: {}, commentOwnerId: {}, requestMemberId: {}",
				comment.getId(), comment.getMemberId(), command.memberId());
			throw new CannotUpdateCommentException();
		}
		comment.setContent(command.content());
	}

	private Comment getComment(final Long commentId) {
		return commentRepository.findById(commentId)
			.orElseThrow(() -> {
				log.info("[CommentUpdateService.getComment] cannot found comment - commentId: {}", commentId);
				return new CannotFoundCommentException();
			});
	}

	private boolean isOwner(final Comment comment, final Long requestMemberId) {
		return comment.getMemberId().equals(requestMemberId);
	}
}
