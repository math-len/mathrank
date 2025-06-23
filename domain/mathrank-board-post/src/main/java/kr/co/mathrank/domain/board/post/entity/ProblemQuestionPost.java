package kr.co.mathrank.domain.board.post.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;

import kr.co.mathrank.domain.board.post.outbox.Outbox;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@TypeAlias("questionPost")
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProblemQuestionPost extends Post {
	@Indexed
	private Long questionId;

	public ProblemQuestionPost(String title, String content, Long ownerId, LocalDateTime createdAt, List<String> images, Long questionId) {
		super(title, content, ownerId, createdAt, images, BoardCategory.PROBLEM_QUESTION);
		this.questionId = questionId;
	}

	public ProblemQuestionPost(String title, String content, Long ownerId, LocalDateTime createdAt, List<String> images, Long questionId, Outbox outbox) {
		super(title, content, ownerId, createdAt, images, BoardCategory.PROBLEM_QUESTION, outbox);
		this.questionId = questionId;
	}
}
