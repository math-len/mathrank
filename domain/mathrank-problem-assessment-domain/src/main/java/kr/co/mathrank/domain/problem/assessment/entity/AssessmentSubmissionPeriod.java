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

	boolean canSubmit(final LocalDateTime submitTime, final boolean isFirstSubmission) {
		if (periodType == AssessmentPeriodType.UNLIMITED) {
			return true;
		}

		// 기간제 문제집일 경우, 한번만 제출할 수 있다
		return isFirstSubmission && startAt.isBefore(submitTime) && endAt.isAfter(submitTime);
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