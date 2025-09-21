package kr.co.mathrank.domain.board.dto;

public record PostPageQuery(
	Long postId,
	Long memberId,

	String title,

	Long singleProblemId,
	Long assessmentId,
	Long contestId
) {
}
