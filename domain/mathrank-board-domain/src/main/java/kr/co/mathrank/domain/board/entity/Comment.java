package kr.co.mathrank.domain.board.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private Post post;

	private Long memberId;

	@Setter
	private String content;

	@CreationTimestamp
	private LocalDateTime createdAt;

	static Comment of(final String content, final Long memberId, final Post post) {
		final Comment comment = new Comment();
		comment.content = content;
		comment.memberId = memberId;
		comment.post = post;

		return comment;
	}
}
