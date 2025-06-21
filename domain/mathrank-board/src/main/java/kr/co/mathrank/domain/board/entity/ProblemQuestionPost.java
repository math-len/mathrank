package kr.co.mathrank.domain.board.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;

import lombok.Getter;
import lombok.ToString;

@TypeAlias("questionPost")
@Getter
@ToString(callSuper = true)
public class ProblemQuestionPost extends Post {
	@Indexed
	private Long questionId;

	public ProblemQuestionPost(String title, String content, Long ownerId, LocalDateTime createdAt, List<String> images, Long questionId) {
		super(title, content, ownerId, createdAt, images, BoardCategory.PROBLEM_QUESTION);
		this.questionId = questionId;
	}
}
