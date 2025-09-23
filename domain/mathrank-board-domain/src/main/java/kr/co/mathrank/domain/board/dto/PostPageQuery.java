package kr.co.mathrank.domain.board.dto;

import kr.co.mathrank.domain.board.entity.PostType;

public record PostPageQuery(
	Long postId,
	Long memberId,
	String nickName,

	PostType postType,

	String title,

	Long singleProblemId,
	Long assessmentId,
	Long contestId
) {
}
