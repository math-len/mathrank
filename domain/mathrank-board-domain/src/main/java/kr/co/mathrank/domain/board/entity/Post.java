package kr.co.mathrank.domain.board.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Setter
	private String title;

	@Setter
	private String content;

	private Long userId;

	@Enumerated(EnumType.STRING)
	private PostType postType;

	private Long contestId;

	private Long assessmentId;

	private Long problemId;

	@CreationTimestamp
	private LocalDateTime createdAt;

	private Post(String title, String content, Long userId) {
		this.title = title;
		this.content = content;
		this.userId = userId;
	}

	public static Post ofFree(String title, String content, Long userId) {
		final Post post = new Post(title, content, userId);
		post.postType = PostType.FREE;

		return post;
	}

	public static Post ofContest(String title, String content, Long userId, Long contestId) {
		final Post post = new Post(title, content, userId);
		post.contestId = contestId;
		post.postType = PostType.CONTEST;

		return post;
	}

	public static Post ofAssessment(String title, String content, Long userId, Long assessmentId) {
		final Post post = new Post(title, content, userId);
		post.assessmentId = assessmentId;
		post.postType = PostType.ASSESSMENT;

		return post;
	}

	public static Post ofSingleProblem(String title, String content, Long userId, Long singelProblemId) {
		final Post post = new Post(title, content, userId);
		post.problemId = singelProblemId;
		post.postType = PostType.SINGLE_PROBLEM;

		return post;
	}
}
