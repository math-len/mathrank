package kr.co.mathrank.domain.board.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Table(indexes = {
	@Index(name = "idx_createdAt_title", columnList = "created_at desc, title"),
	@Index(name = "idx_createdAt_contestId", columnList = "created_at desc, contest_id"),
	@Index(name = "idx_createdAt_assessmentId", columnList = "created_at desc, assessment_id"),
	@Index(name = "idx_createdAt_singleProblemId", columnList = "created_at desc, single_problem_id"),
	@Index(name = "idx_createdAt_nickName", columnList = "created_at desc, member_nick_name"),
	@Index(name = "idx_createdAt_memberId", columnList = "created_at desc, member_id")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Setter
	private String title;

	@Setter
	private String content;

	private Long memberId;

	private String memberNickName;

	@Enumerated(EnumType.STRING)
	private PostType postType;

	private Long contestId;

	private Long assessmentId;

	private Long singleProblemId;

	@CreationTimestamp
	private LocalDateTime createdAt;

	@Builder
	public Post(String title, String content, Long memberId, String memberNickName, PostType postType, Long contestId,
		Long assessmentId, Long singleProblemId) {
		this.title = title;
		this.content = content;
		this.memberId = memberId;
		this.memberNickName = memberNickName;
		this.postType = postType;
		this.contestId = contestId;
		this.assessmentId = assessmentId;
		this.singleProblemId = singleProblemId;
	}

	private Post(String title, String content, Long userId) {
		this.title = title;
		this.content = content;
		this.memberId = userId;
	}

	public static Post of(String title, String content, Long userId) {
		return new Post(title, content, userId);
	}
}
