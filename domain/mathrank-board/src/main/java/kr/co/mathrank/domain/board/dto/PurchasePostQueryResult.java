package kr.co.mathrank.domain.board.dto;

import kr.co.mathrank.domain.board.entity.PurchasePost;
import lombok.Getter;

@Getter
public class PurchasePostQueryResult extends PostQueryResult {
	private final Long purchaseId;

	public PurchasePostQueryResult(final PurchasePost post) {
		super(post.getId(), post.getTitle(), post.getContent(), post.getOwnerId(), post.getCreatedAt(),
			post.getUpdatedAt(), post.getImages());
		this.purchaseId = post.getPurchaseId();
	}
}
