package kr.co.mathrank.domain.problem.assessment.dto;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentPeriodType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
public final class AssessmentDetailQuery {
	@NotNull
	private final Long assessmentId;
	@NotNull
	private final AssessmentPeriodType assessmentPeriodType;

	public static AssessmentDetailQuery periodLimited(final Long id) {
		return new AssessmentDetailQuery(id, AssessmentPeriodType.LIMITED);
	}

	public static AssessmentDetailQuery periodUnLimited(final Long id) {
		return new AssessmentDetailQuery(id, AssessmentPeriodType.UNLIMITED);
	}
}
