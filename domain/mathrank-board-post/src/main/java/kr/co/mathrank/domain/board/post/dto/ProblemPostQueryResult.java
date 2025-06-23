package kr.co.mathrank.domain.board.post.dto;

import kr.co.mathrank.domain.board.post.entity.ProblemQuestionPost;
import lombok.Getter;

@Getter
public class ProblemPostQueryResult extends PostQueryResult {
	private final Long problemId;

	public ProblemPostQueryResult(final ProblemQuestionPost post) {
		super(post.getId(), post.getTitle(), post.getContent(), post.getOwnerId(), post.getCreatedAt(),
			post.getUpdatedAt(), post.getImages());
		this.problemId = post.getQuestionId();
	}
}
