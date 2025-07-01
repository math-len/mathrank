package kr.co.mathrank.domain.board.comment.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Persistable;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment implements Persistable<Long> {
	@Id
	private Long id;

	private Long postId;

	@Setter
	private String content;

	private Long userId;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	@OneToMany(mappedBy = "comment", cascade = CascadeType.PERSIST, orphanRemoval = true)
	@Getter(AccessLevel.NONE)
	private final List<Image> images = new ArrayList<>();

	public static Comment of(final Long id, final Long postId, final String content, final Long userId, final List<String> imageSources) {
		final Comment comment = new Comment();
		comment.id = id;
		comment.postId = postId;
		comment.content = content;
		comment.userId = userId;
		comment.setImages(imageSources);

		return comment;
	}

	public List<String> getImageSources() {
		return images.stream()
			.map(Image::getImageSrc)
			.toList();
	}

	public void updateImages(final List<String> imageSources) {
		this.images.clear();
		this.setImages(imageSources);
	}

	private void setImages(final List<String> images) {
		this.images.addAll(images.stream()
			.map(imageSrc -> Image.of(imageSrc, this))
			.toList());
	}

	@Override
	public boolean isNew() {
		log.debug("isNew() called for Comment with id: {}, result: {}", id, createdAt == null);
		return createdAt == null;
	}

	@PrePersist
	private void prePersist() {
		final LocalDateTime now = LocalDateTime.now();

		if (createdAt == null) {
			createdAt = now;
		}
		if (updatedAt == null) {
			updatedAt = now;
		}
		log.debug("PrePersist called for Comment with id: {}, createdAt: {}, updatedAt: {}", id, createdAt, updatedAt);
	}

	@PreUpdate
	private void preUpdate() {
		updatedAt = LocalDateTime.now();
		log.debug("PreUpdate called for Comment with id: {}, updatedAt: {}", id, updatedAt);
	}
}
