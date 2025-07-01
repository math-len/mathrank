package kr.co.mathrank.domain.board.comment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class Image {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String imageSrc;

	@ManyToOne(fetch = FetchType.LAZY)
	private Comment comment;

	public static Image of(final String imageSrc, final Comment comment) {
		final Image image = new Image();
		image.imageSrc = imageSrc;
		image.comment = comment;

		return image;
	}
}
