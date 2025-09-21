package kr.co.mathrank.domain.board.entity;

import jakarta.persistence.Entity;

@Entity
public class SingleProblemQuestionPost extends Post{
	private Long problemId;
}
