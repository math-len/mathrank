package kr.co.mathrank.domain.board.post.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import kr.co.mathrank.domain.board.post.outbox.Outbox;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Document(collection = "posts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@CompoundIndexes(
	{
		@CompoundIndex(name = "idx_boardCategory_ownerId_createdAt_deleted", def = "{'boardCategory': 1, 'ownerId': 1, 'createdAt': -1, 'deleted': -1}"),
		@CompoundIndex(name = "idx_boardCategory_createdAt_deleted", def = "{'boardCategory': 1, 'createdAt': -1, 'deleted': -1}"),
	}
)
@ToString
public abstract class Post {
	@Id
	private String id;

	@Setter
	private String title;

	@Setter
	private String content;

	private Long ownerId;

	private LocalDateTime createdAt;

	@Setter
	private LocalDateTime updatedAt;

	private List<String> images = new ArrayList<>();

	private BoardCategory boardCategory; // shard key

	@Setter
	private Boolean deleted = false;

	@Indexed(sparse = true)
	@Setter
	private Outbox outbox;

	protected Post(String title, String content, Long ownerId, LocalDateTime createdAt, List<String> images, BoardCategory boardCategory) {
		this.title = title;
		this.content = content;
		this.ownerId = ownerId;
		this.createdAt = createdAt;
		this.updatedAt = createdAt;
		this.images = images;
		this.boardCategory = boardCategory;
	}

	protected Post(String title, String content, Long ownerId, LocalDateTime createdAt, List<String> images, BoardCategory boardCategory, Outbox outbox) {
		this.title = title;
		this.content = content;
		this.ownerId = ownerId;
		this.createdAt = createdAt;
		this.updatedAt = createdAt;
		this.images = images;
		this.boardCategory = boardCategory;
		this.outbox = outbox;
	}

	public List<String> resetImages(List<String> images) {
		final List<String> removedImages = this.images.stream()
			.filter(imagesSource -> !images.contains(imagesSource))
			.toList();
		this.images = images;

		return removedImages;
	}

	public void clearOutbox() {
		this.outbox = null;
	}
}
