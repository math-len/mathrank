package kr.co.mathrank.domain.board.entity;

import java.util.List;

import org.springframework.data.annotation.TypeAlias;

import lombok.Getter;
import lombok.ToString;

@TypeAlias("purchasePost")
@Getter
@ToString(callSuper = true)
public class PurchasePost extends Post {
	private Long purchaseId;

	public PurchasePost(String title, String content, Long ownerId, List<String> images, Long purchaseId) {
		super(title, content, ownerId, images, BoardCategory.PURCHASE_QUESTION);
		this.purchaseId = purchaseId;
	}
}
