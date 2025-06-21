package kr.co.mathrank.domain.board.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.TypeAlias;

import lombok.ToString;

@TypeAlias("freePost")
@ToString(callSuper = true)
public class FreePost extends Post {
	public FreePost(String title, String content, Long ownerId, LocalDateTime createdAt, List<String> images) {
		super(title, content, ownerId,  createdAt, images, BoardCategory.FREE_BOARD);
	}
}
