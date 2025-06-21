package kr.co.mathrank.domain.board.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;

import lombok.Getter;
import lombok.ToString;

@TypeAlias("purchasePost")
@Getter
@ToString(callSuper = true)
public class PurchasePost extends Post {
	@Indexed
	private Long purchaseId;

	public PurchasePost(String title, String content, Long ownerId, LocalDateTime createdAt, List<String> images, Long purchaseId) {
		super(title, content, ownerId, createdAt, images, BoardCategory.PURCHASE_QUESTION);
		this.purchaseId = purchaseId;
	}
}
