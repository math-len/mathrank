package kr.co.mathrank.domain.problem.assessment.entity;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Assessment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE)
	private Long id;

	private Long registerMemberId;

	private String assessmentName;

	@Convert(converter = AssessmentDurationConverter.class)
	private Duration assessmentDuration;

	@OneToMany(mappedBy = "assessment", orphanRemoval = true, cascade = CascadeType.PERSIST)
	private final List<AssessmentItem> assessmentItems = new ArrayList<>();

	@CreationTimestamp
	@Setter(AccessLevel.NONE)
	private LocalDateTime createdAt;

	public void setAssessmentItems(final List<Long> problemIds) {
		this.assessmentItems.clear();
		for (int i = 0; i < problemIds.size(); i++) {
			assessmentItems.add(AssessmentItem.of(i + 1, problemIds.get(i), this));
		}
	}

	public static Assessment of(final Long registerMemberId, final String assessmentName, final Duration assessmentDuration) {
		final Assessment assessment = new Assessment();
		assessment.registerMemberId = registerMemberId;
		assessment.assessmentName = assessmentName;
		assessment.assessmentDuration = assessmentDuration;

		return assessment;
	}
}
