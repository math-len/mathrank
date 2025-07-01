package kr.co.mathrank.domain.board.comment.dto;

import java.util.List;

import kr.co.mathrank.domain.board.comment.entity.Comment;

public record CommentResult(
	Long postId,
	Long commentId,
	String content,
	List<String> images,
	Long userId,
	String createdAt,
	String updatedAt
) {
	public static CommentResult from(final Comment comment) {
		return new CommentResult(
			comment.getPostId(),
			comment.getId(),
			comment.getContent(),
			comment.getImageSources(),
			comment.getUserId(),
			comment.getCreatedAt().toString(),
			comment.getUpdatedAt().toString()
		);
	}
}
