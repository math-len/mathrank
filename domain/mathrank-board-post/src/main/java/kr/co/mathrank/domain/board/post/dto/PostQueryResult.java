package kr.co.mathrank.domain.board.post.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class PostQueryResult {
	private final String postId;
	private final String title;
	private final String content;
	private final Long ownerId;
	private final LocalDateTime createdAt;
	private final LocalDateTime updatedAt;
	private final List<String> images;
}
