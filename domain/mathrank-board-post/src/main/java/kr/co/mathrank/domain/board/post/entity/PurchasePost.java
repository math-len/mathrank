package kr.co.mathrank.domain.board.post.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;

import kr.co.mathrank.domain.board.post.outbox.Outbox;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@TypeAlias("purchasePost")
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PurchasePost extends Post {
	@Indexed
	private Long purchaseId;

	public PurchasePost(String title, String content, Long ownerId, LocalDateTime createdAt, List<String> images, Long purchaseId) {
		super(title, content, ownerId, createdAt, images, BoardCategory.PURCHASE_QUESTION);
		this.purchaseId = purchaseId;
	}

	public PurchasePost(String title, String content, Long ownerId, LocalDateTime createdAt, List<String> images, Long purchaseId, Outbox outbox) {
		super(title, content, ownerId, createdAt, images, BoardCategory.PURCHASE_QUESTION, outbox);
		this.purchaseId = purchaseId;
	}
}
