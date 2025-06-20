package kr.co.mathrank.domain.board.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

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
		@CompoundIndex(name = "idx_boardCategory_ownerId", def = "{'boardCategory': 1, 'ownerId': 1}"),
		@CompoundIndex(name = "idx_boardCategory_createdAt", def = "{'boardCategory': 1, 'createdAt': -1}")
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

	protected Post(String title, String content, Long ownerId, List<String> images, BoardCategory boardCategory) {
		this.title = title;
		this.content = content;
		this.ownerId = ownerId;
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
		this.images = images;
		this.boardCategory = boardCategory;
	}

	public List<String> resetImages(List<String> images) {
		final List<String> removedImages = this.images.stream()
			.filter(imagesSource -> !images.contains(imagesSource))
			.toList();
		this.images = images;

		return removedImages;
	}
}
