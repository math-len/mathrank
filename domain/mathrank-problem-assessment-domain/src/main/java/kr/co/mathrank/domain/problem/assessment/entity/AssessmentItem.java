package kr.co.mathrank.domain.problem.assessment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter(AccessLevel.PACKAGE)
public class AssessmentItem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE)
	private Long id;

	private Integer sequence;

	private Long problemId;

	private Integer score;

	@ManyToOne
	private Assessment assessment;

	public static AssessmentItem of(final Long problemId, final Integer score) {
		final AssessmentItem item = new AssessmentItem();
		item.problemId = problemId;
		item.score = score;

		return item;
	}
}
