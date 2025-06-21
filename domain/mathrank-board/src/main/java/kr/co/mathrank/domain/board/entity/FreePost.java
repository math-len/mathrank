package kr.co.mathrank.domain.board.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.TypeAlias;

import kr.co.mathrank.domain.board.outbox.Outbox;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.ToString;

@TypeAlias("freePost")
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FreePost extends Post {
	public FreePost(String title, String content, Long ownerId, LocalDateTime createdAt, List<String> images) {
		super(title, content, ownerId,  createdAt, images, BoardCategory.FREE_BOARD);
	}

	public FreePost(String title, String content, Long ownerId, LocalDateTime createdAt, List<String> images, Outbox outbox) {
		super(title, content, ownerId,  createdAt, images, BoardCategory.FREE_BOARD, outbox);
	}
}
