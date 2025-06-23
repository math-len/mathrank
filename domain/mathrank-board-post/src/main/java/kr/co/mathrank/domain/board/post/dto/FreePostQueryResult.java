package kr.co.mathrank.domain.board.post.dto;

import kr.co.mathrank.domain.board.post.entity.FreePost;

public class FreePostQueryResult extends PostQueryResult {
	public FreePostQueryResult(final FreePost post) {
		super(post.getId(), post.getTitle(), post.getContent(), post.getOwnerId(), post.getCreatedAt(),
			post.getUpdatedAt(), post.getImages());
	}
}
