package kr.co.mathrank.domain.board.viewcount.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(indexes = @Index(name = "idx_postId_viewCount", columnList = "post_id, view_count"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ViewCount {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "post_id")
	private Long postId;

	@Column(name = "view_count")
	@Setter
	private Long viewCount;

	private LocalDateTime updatedAt;

	public static ViewCount of(final Long postId, final Long viewCount) {
		final ViewCount viewCountEntity = new ViewCount();
		viewCountEntity.postId = postId;
		viewCountEntity.viewCount = viewCount;

		return viewCountEntity;
	}

	@PrePersist
	@PreUpdate
	private void update() {
		updatedAt = LocalDateTime.now();
	}
}
