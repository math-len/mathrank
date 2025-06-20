package kr.co.mathrank.domain.board.entity;

import java.util.List;

import org.springframework.data.annotation.TypeAlias;

import lombok.Getter;
import lombok.ToString;

@TypeAlias("questionPost")
@Getter
@ToString(callSuper = true)
public class ProblemQuestionPost extends Post {
	private Long questionId;

	public ProblemQuestionPost(String title, String content, Long ownerId, List<String> images, Long questionId) {
		super(title, content, ownerId, images, BoardCategory.PROBLEM_QUESTION);
		this.questionId = questionId;
	}
}
