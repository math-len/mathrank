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
@Setter
class AssessmentItem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Integer sequence;

	private Long problemId;

	@ManyToOne
	private Assessment assessment;

	public static AssessmentItem of(final Integer sequence, final Long problemId, final Assessment assessment) {
		final AssessmentItem item = new AssessmentItem();
		item.setSequence(sequence);
		item.setProblemId(problemId);
		item.setAssessment(assessment);

		return item;
	}
}
