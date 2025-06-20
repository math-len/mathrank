package kr.co.mathrank.domain.board.entity;

import java.util.List;

import org.springframework.data.annotation.TypeAlias;

import lombok.ToString;

@TypeAlias("freePost")
@ToString(callSuper = true)
public class FreePost extends Post {
	public FreePost(String title, String content, Long ownerId, List<String> images) {
		super(title, content, ownerId, images, BoardCategory.FREE_BOARD);
	}
}
