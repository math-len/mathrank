package kr.co.mathrank.domain.board.comment.dto;

import java.util.List;

public record CommentResults(
	List<CommentResult> comments
) {
}
