package kr.co.mathrank.domain.problem.assessment.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;

@Embeddable
@Getter
public class AssessmentSubmissionPeriod {
	@Enumerated(EnumType.STRING)
	private AssessmentPeriodType periodType;

	private LocalDateTime startAt;

	private LocalDateTime endAt;

	public boolean canSubmit(final LocalDateTime submitTime) {
		if (periodType == AssessmentPeriodType.UNLIMITED) {
			return true;
		}

		return startAt.isBefore(submitTime) && endAt.isAfter(submitTime);
	}

	static AssessmentSubmissionPeriod unlimited() {
		final AssessmentSubmissionPeriod assessmentSubmissionPeriod = new AssessmentSubmissionPeriod();
		assessmentSubmissionPeriod.periodType = AssessmentPeriodType.UNLIMITED;
		return assessmentSubmissionPeriod;
	}

	static AssessmentSubmissionPeriod limited(final LocalDateTime startAt, final LocalDateTime endAt) {
		final AssessmentSubmissionPeriod assessmentSubmissionPeriod = new AssessmentSubmissionPeriod();
		assessmentSubmissionPeriod.periodType = AssessmentPeriodType.LIMITED;
		assessmentSubmissionPeriod.startAt = startAt;
		assessmentSubmissionPeriod.endAt = endAt;

		return assessmentSubmissionPeriod;
	}
}