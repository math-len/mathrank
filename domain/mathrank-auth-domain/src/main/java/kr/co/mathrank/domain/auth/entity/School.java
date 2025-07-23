package kr.co.mathrank.domain.auth.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class School {
	@Id
	private Long id;

	@ManyToOne
	private Member member;

	private String schoolCode;
}
