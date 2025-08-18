package kr.co.mathrank.domain.problem.assessment.constraint;

import java.util.List;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentItemRegisterCommand;

public class ScoreSumConstraintValidator implements ConstraintValidator<ScoreSum, List<AssessmentItemRegisterCommand>> {
	private static final int SCORE_SUM = 100; // 점수의 총 합 변경지점 최소화를 위해 상수로 설정

	@Override
	public boolean isValid(List<AssessmentItemRegisterCommand> value, ConstraintValidatorContext context) {
		if (value == null) {
			return true;
		}

		final int currentSum = value.stream()
			.map(AssessmentItemRegisterCommand::score)
			.mapToInt(Integer::intValue)
			.sum();
		return currentSum == SCORE_SUM;
	}
}
