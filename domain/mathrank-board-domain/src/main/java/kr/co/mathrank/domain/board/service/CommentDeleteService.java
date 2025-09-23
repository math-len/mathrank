package kr.co.mathrank.domain.board.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.role.Role;
import kr.co.mathrank.domain.board.dto.CommentDeleteCommand;
import kr.co.mathrank.domain.board.entity.Comment;
import kr.co.mathrank.domain.board.entity.Post;
import kr.co.mathrank.domain.board.exception.CannotDeleteCommentException;
import kr.co.mathrank.domain.board.exception.CannotFoundCommentException;
import kr.co.mathrank.domain.board.exception.CannotFoundPostException;
import kr.co.mathrank.domain.board.repository.CommentRepository;
import kr.co.mathrank.domain.board.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class CommentDeleteService {
	private final CommentRepository commentRepository;
	private final PostRepository postRepository;

	@Transactional
	public void delete(@NotNull @Valid final CommentDeleteCommand command) {
		final Comment comment = getCommentForUpdate(command.commentId());
		if (!canDelete(comment, command.requestMemberId(), command.requestMemberRole())) {
			log.info("[CommentDeleteService.delete] cannot delete comment - commentId: {}, commentOwnerId: {}, requestMemberId: {}, requestMemberRole: {}",
				comment.getId(), comment.getMemberId(), command.requestMemberId(), command.requestMemberRole());
			throw new CannotDeleteCommentException();
		}
		postRepository.decreaseCommentCount(comment.getPost().getId());
		commentRepository.delete(comment);
	}

	private Comment getCommentForUpdate(final Long commentId) {
		return commentRepository.findCommentWithPostForUpdate(commentId)
			.orElseThrow(() -> {
				log.info("[CommentDeleteService.getComment] cannot found comment - commentId: {}", commentId);
				return new CannotFoundCommentException();
			});
	}

	private boolean canDelete(final Comment comment, final Long requestMemberId, final Role role) {
		// 본인 댓글이면 삭제 가능!
		if (comment.getMemberId().equals(requestMemberId)) {
			return true;
		}

		// 관리자면 삭제 가능!
		if (role == Role.ADMIN) {
			return true;
		}

		return false;
	}
}
