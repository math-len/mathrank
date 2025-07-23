package kr.co.mathrank.domain.auth.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class School {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private Member member;

	private String schoolCode;

	public static School of(Member member, String schoolCode) {
		final School school = new School();
		school.schoolCode = schoolCode;
		school.member = member;

		return school;
	}
}
