package kr.co.mathrank.domain.board.dto;

import java.time.LocalDateTime;

import kr.co.mathrank.domain.board.entity.Post;
import kr.co.mathrank.domain.board.entity.PostType;

public record PostPageQueryResult(
	Long postId,
	Long memberId,
	String memberNickName,
	PostType postType,

	String title,

	Long singleProblemId,
	Long assessmentId,
	Long contestId,

	Integer commentCount,

	LocalDateTime createdAt
) {
	public static PostPageQueryResult from(final Post post) {
		return new PostPageQueryResult(
			post.getId(),
			post.getMemberId(),
			post.getMemberNickName(),
			post.getPostType(),
			post.getTitle(),
			post.getSingleProblemId(),
			post.getAssessmentId(),
			post.getContestId(),
			post.getCommentCount(),
			post.getCreatedAt()
		);
	}
}
