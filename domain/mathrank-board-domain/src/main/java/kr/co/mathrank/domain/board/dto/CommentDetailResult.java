package kr.co.mathrank.domain.board.dto;

import java.time.LocalDateTime;

import kr.co.mathrank.domain.board.entity.Comment;

public record CommentDetailResult(
	Long commentId,
	Long memberId,
	String content,
	LocalDateTime createdAt
) {
	public static CommentDetailResult from(final Comment comment) {
		return new CommentDetailResult(comment.getId(), comment.getMemberId(), comment.getContent(), comment.getCreatedAt());
	}
}
