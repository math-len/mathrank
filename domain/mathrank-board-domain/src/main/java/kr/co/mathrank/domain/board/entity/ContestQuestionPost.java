package kr.co.mathrank.domain.board.entity;

import jakarta.persistence.Entity;

@Entity
public class ContestQuestionPost extends Post{
	private Long contestId;
}
