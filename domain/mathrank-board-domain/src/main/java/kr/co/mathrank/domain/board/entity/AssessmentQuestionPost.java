package kr.co.mathrank.domain.board.entity;

import jakarta.persistence.Entity;

@Entity
public class AssessmentQuestionPost extends Post {
	private Long assessmentId;
}
