package kr.co.mathrank.domain.board.dto;

import java.time.LocalDateTime;
import java.util.List;

import kr.co.mathrank.domain.board.entity.Post;
import kr.co.mathrank.domain.board.entity.PostType;

public record PostDetailQueryResult(
	Long postId,
	Long memberId,
	String memberNickName,
	PostType postType,

	String title,
	String content,

	List<CommentDetailResult> comments,

	Long singleProblemId,
	Long assessmentId,
	Long contestId,

	LocalDateTime createdAt
) {
	public static PostDetailQueryResult from(final Post post) {
		return new PostDetailQueryResult(
			post.getId(),
			post.getMemberId(),
			post.getMemberNickName(),
			post.getPostType(),
			post.getTitle(),
			post.getContent(),
			post.getComments().stream()
				.map(CommentDetailResult::from)
				.toList(),
			post.getSingleProblemId(),
			post.getAssessmentId(),
			post.getContestId(),
			post.getCreatedAt()
		);
	}
}
